package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Connection;
import io.nats.client.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Singleton
public class NatsBrokerSender {

    private static final int REQUEST_TIMEOUT_SECONDS = 5;

    private ObjectMapper objectMapper;
    private NatsUtils natsUtils;

    @Inject
    private void initialize(ObjectMapper objectMapper, NatsUtils natsUtils) {
        this.objectMapper = objectMapper;
        this.natsUtils = natsUtils;
    }

    public void deleteInfractionsByRegistrationNumber(String regNumber, String jwtToken) {
        try {
            // Napravi payload sa tokenom
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("jwtToken", jwtToken);

            String payloadStr = objectMapper.writeValueAsString(payload);

            Connection connection = natsUtils.getConnection();
            if (connection == null) {
                throw new RuntimeException("NATS connection is not initialized");
            }

            String subject = "infractions.deleteByRegNumber." + regNumber;

            // Pošalji request i čekaj odgovor
            String response = sendRequest(subject, payloadStr);

            System.out.printf("🗑️ Delete infractions response for registration number %s: %s%n", regNumber, response);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete infractions for registration number " + regNumber, e);
        }
    }



    private String sendRequest(String subject, String payload) {
        Connection connection = natsUtils.getConnection();
        if (connection == null) {
            throw new RuntimeException("NATS connection is not initialized");
        }

        try {
            CompletableFuture<Message> future = connection.request(subject, payload.getBytes(StandardCharsets.UTF_8));

            Message msg = future.get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // 👇 Ovde čitaš body kao string
            String body = new String(msg.getData(), StandardCharsets.UTF_8);

            // 👇 Ako želiš da vidiš headers
            if (msg.hasHeaders()) {
                msg.getHeaders().forEach((key, values) -> {
                    System.out.println("Header: " + key + " = " + values);
                });
            }

            return body;
        } catch (TimeoutException e) {
            throw new RuntimeException("NATS request timeout for subject: " + subject, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("NATS request interrupted for subject: " + subject, e);
        } catch (Exception e) {
            throw new RuntimeException("NATS request failed for subject: " + subject, e);
        }
    }
}
