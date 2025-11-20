package com.radovan.play.brokers

import com.fasterxml.jackson.databind.ObjectMapper
import com.radovan.play.dto.InfractionDto
import com.radovan.play.services.InfractionService
import com.radovan.play.utils.NatsUtils
import io.nats.client.{Dispatcher, MessageHandler}
import jakarta.inject.{Inject, Singleton}

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
  }

  private val addInfraction: MessageHandler = msg => {
    try {
      val payload = new String(msg.getData, "UTF-8")
      val infractionDto = objectMapper.readValue(payload, classOf[InfractionDto])

      val storedDto = infractionService.addInfraction(infractionDto)

      // Ako je request/reply pattern, odgovori nazad
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

}
