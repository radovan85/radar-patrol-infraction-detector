package com.radovan.play.converter

import com.radovan.play.dto.InfractionDto
import com.radovan.play.entity.InfractionEntity
import com.radovan.play.utils.TimeConversionUtils
import jakarta.inject.{Inject, Singleton}
import org.modelmapper.ModelMapper

@Singleton
class TempConverter {

  private var mapper:ModelMapper = _
  private var conversionUtils:TimeConversionUtils = _

  @Inject
  private def initialize(mapper: ModelMapper, conversionUtils: TimeConversionUtils): Unit = {
    this.mapper = mapper
    this.conversionUtils = conversionUtils
  }


  def entityToDto(infractionEntity: InfractionEntity):InfractionDto = {
    val returnValue = mapper.map(infractionEntity,classOf[InfractionDto])
    val infractionTimeOption = Option(infractionEntity.getInfractionTime())
    infractionTimeOption match {
      case Some(infractionTime) => returnValue.setInfractionTimeStr(conversionUtils.timestampToString(infractionTime))
      case None =>
    }

    returnValue
  }

  def dtoToEntity(infractionDto: InfractionDto):InfractionEntity = {
    val returnValue = mapper.map(infractionDto,classOf[InfractionEntity])
    val infractionTimeStrOption = Option(infractionDto.getInfractionTimeStr())
    infractionTimeStrOption match {
      case Some(infractionTimeStr) => returnValue.setInfractionTime(conversionUtils.stringToTimestamp(infractionTimeStr))
      case None =>
    }

    returnValue
  }
}
