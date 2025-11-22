package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.services.VehicleService;
import com.radovan.play.utils.JwtUtil;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import io.nats.client.impl.Headers;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;

@Singleton
public class NatsBrokerListener {

    private NatsUtils natsUtils;
    private ObjectMapper objectMapper;
    private VehicleService vehicleService;
    private static final String ContentTypeHeader = "Content-Type";
    private static final String ApplicationJson = "application/json";
    private JwtUtil jwtUtil;

    @Inject
    private void initialize(NatsUtils natsUtils, ObjectMapper objectMapper, VehicleService vehicleService,JwtUtil jwtUtil) {
        this.natsUtils = natsUtils;
        this.objectMapper = objectMapper;
        this.vehicleService = vehicleService;
        this.jwtUtil = jwtUtil;
        initListeners();
    }


    private void initListeners() {
        Connection connection = natsUtils.getConnection();
        if (connection != null) {
            Dispatcher dispatcher = connection.createDispatcher();
            dispatcher.subscribe("vehicles.getAll", getAllVehicles);
        } else {
            System.err.println("*** NATS connection unavailable — order listener not initialized");
        }
    }

    private MessageHandler getAllVehicles = msg -> {
        try {
            String payloadStr = new String(msg.getData(), StandardCharsets.UTF_8);

            // Parsiraj JSON u JsonNode
            JsonNode root = objectMapper.readTree(payloadStr);

            // Izvuci jwtToken iz poruke
            String jwtToken = root.has("jwtToken") ? root.get("jwtToken").asText() : "";

            // ✅ Validiraj token preko JwtUtil
            boolean valid = jwtUtil.validateToken(jwtToken).join();
            if (!valid) {
                sendErrorResponse(msg.getReplyTo(), "Unauthorized: invalid token", 401);
                return;
            }

            // Ako je token validan, možeš izvući i userId/roles ako ti trebaju
            //Optional<String> userIdOpt = jwtUtil.extractUsername(jwtToken).join();
            //Optional<List<String>> rolesOpt = jwtUtil.extractRoles(jwtToken).join();

            // Uzimamo vozila iz servisa
            var vehicles = vehicleService.listAll();

            // Konvertujemo u JSON
            byte[] responsePayload = objectMapper.writeValueAsBytes(vehicles);

            Headers headers = new Headers();
            headers.add(ContentTypeHeader, ApplicationJson);

            if (msg.getReplyTo() != null) {
                natsUtils.getConnection().publish(msg.getReplyTo(), headers, responsePayload);
            } else {
                System.out.println("No reply subject provided for vehicles.getAll");
            }

            //System.out.printf("✅ Vehicles fetched successfully for user %s%n", userIdOpt.orElse("unknown"));

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(msg.getReplyTo(), "Failed to fetch vehicles: " + e.getMessage(), 500);
        }
    };




    private int extractIdFromSubject(String subject, String prefix) {
        try {
            return Integer.parseInt(subject.replace(prefix, ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void sendErrorResponse(String replyTo, String message, int status) {
        try {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("status", status);
            errorNode.put("message", message);

            Headers headers = new Headers();
            headers.add(ContentTypeHeader, ApplicationJson);

            System.out.println("ERROR: " + message);
            natsUtils.getConnection().publish(replyTo, headers, objectMapper.writeValueAsBytes(errorNode));
        } catch (Exception e) {
            try {
                natsUtils.getConnection().publish(replyTo, new byte[0]);
            } catch (Exception ex) {
                System.err.println("Failed to send error response: " + ex.getMessage());
            }
        }
    }
}
