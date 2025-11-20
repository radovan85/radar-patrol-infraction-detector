package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.services.VehicleService;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import io.nats.client.impl.Headers;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class NatsBrokerListener {

    private NatsUtils natsUtils;
    private ObjectMapper objectMapper;
    private VehicleService vehicleService;
    private static final String ContentTypeHeader = "Content-Type";
    private static final String ApplicationJson = "application/json";

    @Inject
    private void initialize(NatsUtils natsUtils, ObjectMapper objectMapper, VehicleService vehicleService) {
        this.natsUtils = natsUtils;
        this.objectMapper = objectMapper;
        this.vehicleService = vehicleService;
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
            // Uzimamo sve vozila iz servisa
            var vehicles = vehicleService.listAll();

            // Konvertujemo u JSON
            byte[] payload = objectMapper.writeValueAsBytes(vehicles);

            // Dodajemo header da je JSON
            Headers headers = new Headers();
            headers.add(ContentTypeHeader, ApplicationJson);

            // Ako postoji replyTo, šaljemo nazad
            if (msg.getReplyTo() != null) {
                natsUtils.getConnection().publish(msg.getReplyTo(), headers, payload);
            } else {
                System.out.println("No reply subject provided for vehicles.getAll");
            }
        } catch (Exception e) {
            // Ako nešto pukne, šaljemo error response
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
