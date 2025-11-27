package com.radovan.spring.brokers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.RadarDto;
import com.radovan.spring.services.RadarService;
import com.radovan.spring.utils.NatsUtils;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class RadarNatsListener {

	private ObjectMapper objectMapper;
	private NatsUtils natsUtils;
	private RadarService radarService;
	private Connection connection;

	@Autowired
	private void initialize(ObjectMapper objectMapper, NatsUtils natsUtils, RadarService radarService) {
		this.objectMapper = objectMapper;
		this.natsUtils = natsUtils;
		this.radarService = radarService;
		this.connection = natsUtils.getConnection();
		initializeListener();
	}

	private void initializeListener() {
		Dispatcher dispatcher = connection.createDispatcher();
		dispatcher.subscribe("radar.getRadar.*", onGetRadar);
	}

	private final MessageHandler onGetRadar = msg -> {
		try {
			// Parse payload (ako ti treba dodatni info iz poruke)
			// JsonNode payload = objectMapper.readTree(msg.getData());

			// Extract radarId from subject
			long radarId = extractIdFromSubject(msg.getSubject(), "radar.getRadar.");

			// Get radar DTO
			RadarDto radarDto = radarService.getRadarById(radarId);

			// Prepare reply
			String replyTo = (msg.getReplyTo() != null && !msg.getReplyTo().isEmpty()) ? msg.getReplyTo()
					: "radar.response";

			connection.publish(replyTo, objectMapper.writeValueAsBytes(radarDto));

		} catch (Exception ex) {
			try {
				ObjectNode errorNode = objectMapper.createObjectNode();
				errorNode.put("status", 500);
				errorNode.put("error", "Failed to retrieve radar: " + ex.getMessage());

				String replyTo = (msg.getReplyTo() != null && !msg.getReplyTo().isEmpty()) ? msg.getReplyTo()
						: "radar.response";

				publishResponse(replyTo, errorNode);
			} catch (Exception innerEx) {
				innerEx.printStackTrace();
			}
		}
	};

	private long extractIdFromSubject(String subject, String prefix) {
		try {
			return Long.parseLong(subject.replace(prefix, ""));
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	private void publishResponse(String replyTo, ObjectNode node) {
		if (replyTo != null && !replyTo.isEmpty()) {
			try {
				byte[] bytes = objectMapper.writeValueAsBytes(node);
				connection.publish(replyTo, bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
