package com.radovan.play.dto

import scala.beans.BeanProperty

@SerialVersionUID(1L)
class InfractionDto extends Serializable{

  @BeanProperty var id:java.lang.Long = _

  @BeanProperty var infractionTimeStr:String = _

  @BeanProperty var vehicleRegistrationNumber:String = _

  @BeanProperty var vehicleSpeed:java.lang.Integer = _

  @BeanProperty var radarId:java.lang.Long = _

  @BeanProperty var infractionAmount:java.lang.Long = _
}
