package com.radovan.play.repository

import com.radovan.play.entity.InfractionEntity

trait InfractionRepository {

  def save(infractionEntity: InfractionEntity):InfractionEntity

  def findById(infractionId:Long):Option[InfractionEntity]

  def findAll:Array[InfractionEntity]
}
