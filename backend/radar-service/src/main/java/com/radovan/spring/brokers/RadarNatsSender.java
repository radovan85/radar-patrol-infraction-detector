package com.radovan.spring.brokers;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.utils.NatsUtils;

import io.nats.client.Connection;
import io.nats.client.Message;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class RadarNatsSender {

	private static final int REQUEST_TIMEOUT_SECONDS = 5;

	private ObjectMapper objectMapper;
	private NatsUtils natsUtils;

	@Autowired
	private void initialize(ObjectMapper objectMapper, NatsUtils natsUtils) {
		this.objectMapper = objectMapper;
		this.natsUtils = natsUtils;
	}

	public JsonNode[] retrieveAllVehicles(String jwtToken) {
		ObjectNode payload = objectMapper.createObjectNode();
		payload.put("jwtToken", jwtToken);
		String response = sendRequest("vehicles.getAll", payload.toString());
		JsonNode jsonNode;
		try {
			jsonNode = objectMapper.readTree(response);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse response", e);
		}

		if (jsonNode.has("status") && jsonNode.get("status").asInt() == 500) {
			String msg = jsonNode.has("error") ? jsonNode.get("error").stringValue() : "Failed to retrieve cart items";
			throw new RuntimeException(msg);
		}

		JsonNode vehiclesNode;
		if (jsonNode.isArray()) {
			vehiclesNode = jsonNode;
		} else if (jsonNode.has("vehicles") && jsonNode.get("vehicles").isArray()) {
			vehiclesNode = jsonNode.get("vehicles");
		} else {
			throw new RuntimeException("Expected array of vehicles, but got: " + jsonNode.getNodeType());
		}

		return StreamSupport.stream(vehiclesNode.spliterator(), false).toArray(JsonNode[]::new);
	}

	public void sendInfraction(JsonNode infractionJson, String jwtToken) {
		try {
			// Napravi wrapper JSON objekat
			ObjectNode wrapper = objectMapper.createObjectNode();
			wrapper.put("jwtToken", jwtToken);
			wrapper.set("infraction", infractionJson);

			String payload = objectMapper.writeValueAsString(wrapper);

			Connection connection = natsUtils.getConnection();
			if (connection == null) {
				throw new RuntimeException("NATS connection is not initialized");
			}

			connection.publish("infraction.create", payload.getBytes(StandardCharsets.UTF_8));

		} catch (Exception e) {
			throw new RuntimeException("Failed to send infraction", e);
		}
	}

	public void deleteInfractionsByRadarId(Long radarId, String jwtToken) {
		try {
			// Napravi payload sa tokenom
			ObjectNode payload = objectMapper.createObjectNode();
			payload.put("jwtToken", jwtToken);

			String payloadStr = objectMapper.writeValueAsString(payload);

			Connection connection = natsUtils.getConnection();
			if (connection == null) {
				throw new RuntimeException("NATS connection is not initialized");
			}

			// Subject je dinamičan: infractions.deleteByRadarId.{radarId}
			String subject = "infractions.deleteByRadarId." + radarId;

			// Pošalji request i čekaj odgovor
			String response = sendRequest(subject, payloadStr);

			System.out.printf("🗑️ Delete infractions response for radar %d: %s%n", radarId, response);

		} catch (Exception e) {
			throw new RuntimeException("Failed to delete infractions for radarId " + radarId, e);
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
