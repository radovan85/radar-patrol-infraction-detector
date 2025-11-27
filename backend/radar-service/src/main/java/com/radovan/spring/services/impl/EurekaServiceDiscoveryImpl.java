package com.radovan.spring.services.impl;

import com.radovan.spring.services.EurekaServiceDiscovery;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EurekaServiceDiscoveryImpl implements EurekaServiceDiscovery {

	private static final String EUREKA_SERVER_URL = "http://localhost:8761/eureka/apps";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public String getServiceUrl(String serviceName) {
		String url = EUREKA_SERVER_URL + "/" + serviceName;
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

		String responseBody = response.getBody();

		if (responseBody == null) {
			throw new RuntimeException("Service not found: " + serviceName);
		}

		try {
			// Parsiranje JSON-a u JsonNode
			JsonNode root = objectMapper.readTree(responseBody);

			JsonNode application = root.get("application");
			if (application == null) {
				throw new RuntimeException("Service not found: " + serviceName);
			}

			JsonNode instanceNode = application.get("instance");
			JsonNode instance;

			if (instanceNode.isArray()) {
				instance = instanceNode.get(0);
			} else {
				instance = instanceNode;
			}

			return instance.get("homePageUrl").stringValue();

		} catch (Exception e) {
			throw new RuntimeException("Failed to parse Eureka response", e);
		}
	}
}
