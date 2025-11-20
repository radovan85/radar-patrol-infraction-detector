package com.radovan.play.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.play.services.EurekaServiceDiscovery;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import java.util.Iterator;
import java.util.Optional;

@Singleton
public class EurekaServiceDiscoveryImpl implements EurekaServiceDiscovery {

    private static final String EUREKA_API_SERVICES_URL = "http://localhost:8761/eureka/apps";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WSClient wsClient;

    @Inject
    public EurekaServiceDiscoveryImpl(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    @Override
    public String getServiceUrl(String serviceName) {
        WSResponse response = wsClient.url(EUREKA_API_SERVICES_URL)
                .addHeader("Accept", "application/json")
                .get()
                .toCompletableFuture()
                .join();

        JsonNode responseJson = response.asJson();

        if (responseJson == null || responseJson.isEmpty()) {
            throw new RuntimeException("No services found in Eureka registry");
        }

        JsonNode applicationsNode = responseJson.get("applications");
        if (applicationsNode == null) {
            throw new RuntimeException("No applications node in Eureka response");
        }

        JsonNode appsNode = applicationsNode.get("application");
        if (appsNode == null || !appsNode.isArray()) {
            throw new RuntimeException("No application array in Eureka response");
        }

        Iterator<JsonNode> apps = appsNode.elements();
        while (apps.hasNext()) {
            JsonNode app = apps.next();
            String appName = Optional.ofNullable(app.get("name"))
                    .map(JsonNode::asText)
                    .orElse(null);

            if (appName != null && appName.equalsIgnoreCase(serviceName)) {
                JsonNode instancesNode = app.get("instance");
                if (instancesNode != null && instancesNode.isArray()) {
                    Iterator<JsonNode> instances = instancesNode.elements();
                    while (instances.hasNext()) {
                        JsonNode instance = instances.next();
                        String address = Optional.ofNullable(instance.get("hostName"))
                                .map(JsonNode::asText)
                                .orElse(null);
                        Integer port = Optional.ofNullable(instance.get("port"))
                                .map(p -> p.get("$"))
                                .map(JsonNode::asInt)
                                .orElse(null);

                        if (address != null && port != null) {
                            return "http://" + address + ":" + port;
                        }
                    }
                }
            }
        }

        throw new RuntimeException("Service not found: " + serviceName);
    }
}
