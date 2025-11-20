package com.radovan.play.services

import com.radovan.play.dto.InfractionDto

trait InfractionService {

  def addInfraction(infractionDto: InfractionDto):InfractionDto

  def getInfractionById(infractionId:Long):InfractionDto

  def listAll:Array[InfractionDto]
}
