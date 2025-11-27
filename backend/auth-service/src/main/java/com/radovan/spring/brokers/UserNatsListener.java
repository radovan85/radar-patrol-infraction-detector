package com.radovan.spring.brokers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.UserDto;
import com.radovan.spring.exceptions.SuspendedUserException;
import com.radovan.spring.services.UserService;
import com.radovan.spring.utils.NatsUtils;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Component
public class UserNatsListener {

	private ObjectMapper objectMapper;
	private NatsUtils natsUtils;
	private UserService userService;
	private Connection connection;

	@Autowired
	private void initialize(ObjectMapper objectMapper, NatsUtils natsUtils, UserService userService) {
		this.objectMapper = objectMapper;
		this.natsUtils = natsUtils;
		this.userService = userService;
		this.connection = natsUtils.getConnection();
		initializeListener();
	}

	private void initializeListener() {
		Dispatcher dispatcher = connection.createDispatcher();
		dispatcher.subscribe("user.getCurrent.*", onGetCurrentUser);
	}

	private final MessageHandler onGetCurrentUser = msg -> {
		try {
			// Get user DTO
			UserDto userDto = userService.getCurrentUser();

			// Prepare reply
			String replyTo = (msg.getReplyTo() != null && !msg.getReplyTo().isEmpty()) ? msg.getReplyTo()
					: "radar.response";

			connection.publish(replyTo, objectMapper.writeValueAsBytes(userDto));

		} catch (SuspendedUserException suspendedEx) {
			try {
				ObjectNode errorNode = objectMapper.createObjectNode();
				errorNode.put("status", 451);
				errorNode.put("error", suspendedEx.getMessage());

				String replyTo = (msg.getReplyTo() != null && !msg.getReplyTo().isEmpty()) ? msg.getReplyTo()
						: "user.response";

				publishResponse(replyTo, errorNode);
			} catch (Exception innerEx) {
				innerEx.printStackTrace();
			}
		} catch (Exception ex) {
			try {
				ObjectNode errorNode = objectMapper.createObjectNode();
				errorNode.put("status", 500);
				errorNode.put("error", "Failed to retrieve user: " + ex.getMessage());

				String replyTo = (msg.getReplyTo() != null && !msg.getReplyTo().isEmpty()) ? msg.getReplyTo()
						: "user.response";

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
