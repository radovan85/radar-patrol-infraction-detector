package com.radovan.play.services

import com.radovan.play.dto.InfractionDto

trait InfractionService {

  def addInfraction(infractionDto: InfractionDto,jwtToken:String):InfractionDto

  def getInfractionById(infractionId:Long):InfractionDto

  def listAll:Array[InfractionDto]

  def deleteAllByRadarId(radarId:Long,jwtToken:String):Unit
}
