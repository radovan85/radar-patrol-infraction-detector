package com.radovan.play.services.impl

import com.radovan.play.brokers.InfractionNatsSender
import com.radovan.play.converter.TempConverter
import com.radovan.play.dto.InfractionDto
import com.radovan.play.exceptions.InstanceUndefinedException
import com.radovan.play.repository.InfractionRepository
import com.radovan.play.services.InfractionService
import com.radovan.play.utils.TimeConversionUtils
import jakarta.inject.{Inject, Singleton}

@Singleton
class InfractionServiceImpl extends InfractionService{

  private var infractionRepository:InfractionRepository = _
  private var tempConverter:TempConverter = _
  private var conversionUtils:TimeConversionUtils = _
  private var natsSender:InfractionNatsSender = _

  @Inject
  private def initialize(infractionRepository: InfractionRepository,tempConverter: TempConverter,
                         conversionUtils: TimeConversionUtils,natsSender: InfractionNatsSender):Unit = {
    this.infractionRepository = infractionRepository
    this.tempConverter = tempConverter
    this.conversionUtils = conversionUtils
    this.natsSender = natsSender
  }

  override def addInfraction(infractionDto: InfractionDto,jwtToken:String): InfractionDto = {
    // 1. Preuzmi radar payload preko NATS-a
    val radarPayload = natsSender.retrieveRadarById(infractionDto.getRadarId(),jwtToken)

    // 2. Izvuci maxSpeed iz payload-a
    val maxSpeed = radarPayload.get("maxSpeed").asInt()

    // 3. Izračunaj amount na osnovu brzine vozila i maxSpeed-a
    val infractionAmount: Long = calculateInfractionAmount(
      infractionDto.getVehicleSpeed(),
      maxSpeed
    )

    // 4. Podesi amount u DTO
    infractionDto.setInfractionAmount(infractionAmount)

    // 5. Konvertuj u entitet i snimi u bazu
    val infractionEntity = tempConverter.dtoToEntity(infractionDto)
    infractionEntity.setInfractionTime(conversionUtils.getCurrentUTCTimestamp)

    val storedInfraction = infractionRepository.save(infractionEntity)
    tempConverter.entityToDto(storedInfraction)
  }

  override def getInfractionById(infractionId: Long): InfractionDto = {
    infractionRepository.findById(infractionId) match {
      case Some(infractionEntity) => tempConverter.entityToDto(infractionEntity)
      case None => throw new InstanceUndefinedException("The infraction has not been found!")
    }

  }

  override def listAll: Array[InfractionDto] = {
    infractionRepository.findAll.collect{
      case infractionEntity => tempConverter.entityToDto(infractionEntity)
    }
  }

  private def calculateInfractionAmount(speed: Int, maxSpeed: Int): Long = {
    val excess = speed - maxSpeed
    if (excess <= 0) {
      0L // nema prekršaja
    } else if (excess <= 10) {
      5000L // do 10 km/h preko
    } else if (excess <= 25) {
      10000L // od 11 do 25 km/h preko
    } else if (excess <= 40) {
      20000L // od 26 do 40 km/h preko
    } else {
      40000L // preko 40 km/h
    }
  }

  override def deleteAllByRadarId(radarId: Long,jwtToken:String): Unit = {
    infractionRepository.deleteAllByRadarId(radarId)
  }

  override def countInfractions: Long = infractionRepository.count

  override def deleteAllByRegNumber(regNumber: String, jwtToken: String): Unit = {
    infractionRepository.deleteAllByRegNumber(regNumber)
  }
}
