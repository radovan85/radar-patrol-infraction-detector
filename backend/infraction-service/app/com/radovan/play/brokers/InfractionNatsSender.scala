package com.radovan.play.brokers

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.radovan.play.utils.NatsUtils
import io.nats.client.Message
import jakarta.inject.{Inject, Singleton}

import java.nio.charset.StandardCharsets
import java.util.concurrent.{CompletableFuture, TimeUnit}

@Singleton
class InfractionNatsSender @Inject()(
                                     objectMapper: ObjectMapper,
                                     natsUtils: NatsUtils
                                   )  {

  private val REQUEST_TIMEOUT_SECONDS = 5

  def retrieveRadarById(radarId:Long):JsonNode = {
    val payload = objectMapper.createObjectNode()
      //.put("token", jwtToken)
      .put("radarId", radarId)

    try {
      val response = sendRequest(s"radar.getRadar.$radarId", payload.toString)
      val json = objectMapper.readTree(response)

      json
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"Error retrieving radar: ${e.getMessage}", e)
    }
  }

  private def sendRequest(subject: String, payload: String): String = {
    val connection = natsUtils.getConnection
    if (connection == null) throw new RuntimeException("NATS connection is not initialized")

    try {
      val future: CompletableFuture[Message] =
        connection.request(subject, payload.getBytes(StandardCharsets.UTF_8))

      val msg = future.get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
      new String(msg.getData, StandardCharsets.UTF_8)
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"NATS request failed for subject: $subject", e)
    }
  }
}
