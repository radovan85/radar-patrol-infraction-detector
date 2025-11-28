package com.radovan.play.brokers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.radovan.play.dto.InfractionDto
import com.radovan.play.services.InfractionService
import com.radovan.play.utils.NatsUtils
import io.nats.client.{Dispatcher, Message, MessageHandler}
import jakarta.inject.{Inject, Singleton}

import scala.util.Try

@Singleton
class InfractionNatsListener @Inject()(
                                        objectMapper: ObjectMapper,
                                        natsUtils: NatsUtils,
                                        infractionService: InfractionService
                                      ){

  private val connection = natsUtils.getConnection

  @Inject
  private def init(): Unit = {
    val dispatcher: Dispatcher = connection.createDispatcher()
    dispatcher.subscribe("infraction.create", addInfraction)
    dispatcher.subscribe("infractions.deleteByRadarId.*", deleteInfractionsByRadarId)
    dispatcher.subscribe("infractions.deleteByRegNumber.*", deleteInfractionsByRegNumber)
  }

  private val addInfraction: MessageHandler = msg => {
    try {
      val payload = new String(msg.getData, "UTF-8")

      // Parsiraj JSON u JsonNode
      val rootNode = objectMapper.readTree(payload)

      // Izvuci token
      val jwtToken = Option(rootNode.get("jwtToken")).map(_.asText()).getOrElse("")

      // Izvuci DTO deo
      val infractionNode = rootNode.get("infraction")
      val infractionDto = objectMapper.treeToValue(infractionNode, classOf[InfractionDto])

      val storedDto = infractionService.addInfraction(infractionDto, jwtToken)

      if (msg.getReplyTo != null) {
        val response = objectMapper.writeValueAsBytes(storedDto)
        connection.publish(msg.getReplyTo, response)
      }

      println(s"✅ Infraction stored for vehicle ${infractionDto.getVehicleRegistrationNumber()} at speed ${infractionDto.getVehicleSpeed()}")

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        println("❌ Failed to process infraction message")
    }
  }

  private val deleteInfractionsByRadarId: MessageHandler = (msg: Message) => {
    try {
      val radarId = extractIdFromSubject(msg.getSubject, "infractions.deleteByRadarId.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("jwtToken")).map(_.asText()).getOrElse("")
      infractionService.deleteAllByRadarId(radarId,jwtToken)

      val responseNode = objectMapper.createObjectNode()
      responseNode.put("status", 200)
      responseNode.put("message", s"All infractions for radar ID $radarId deleted successfully")

      publishResponse(msg.getReplyTo, responseNode)
    } catch {
      case ex: Exception =>
        val errorJson = objectMapper.createObjectNode()
        errorJson.put("status", 500)
        errorJson.put("message", s"Error removing infractions: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, errorJson)
    }
  }

  private val deleteInfractionsByRegNumber: MessageHandler = (msg: Message) => {
    try {
      val regNumber = extractRegNumberFromSubject(msg.getSubject, "infractions.deleteByRegNumber.")
      val payload = objectMapper.readTree(msg.getData)
      val jwtToken = Option(payload.get("jwtToken")).map(_.asText()).getOrElse("")
      infractionService.deleteAllByRegNumber(regNumber, jwtToken)

      val responseNode = objectMapper.createObjectNode()
      responseNode.put("status", 200)
      responseNode.put("message", s"All infractions for vehicle $regNumber deleted successfully")

      publishResponse(msg.getReplyTo, responseNode)
    } catch {
      case ex: Exception =>
        val errorJson = objectMapper.createObjectNode()
        errorJson.put("status", 500)
        errorJson.put("message", s"Error removing infractions: ${ex.getMessage}")
        publishResponse(msg.getReplyTo, errorJson)
    }
  }

  private def extractRegNumberFromSubject(subject: String, prefix: String): String = {
    val regStr = subject.stripPrefix(prefix)
    if (regStr.isEmpty) throw new IllegalArgumentException(s"Invalid registrationNumber in subject: $subject")
    regStr
  }

  private def extractIdFromSubject(subject: String, prefix: String): Long = {
    val idStr = subject.stripPrefix(prefix)
    Try(idStr.toLong).getOrElse(throw new IllegalArgumentException(s"Invalid radarId in subject: $subject"))
  }


  private def publishResponse(replyTo: String, node: ObjectNode): Unit = {
    if (replyTo != null && replyTo.nonEmpty) {
      val bytes = objectMapper.writeValueAsBytes(node)
      natsUtils.getConnection.publish(replyTo, bytes)
    }
  }


}
