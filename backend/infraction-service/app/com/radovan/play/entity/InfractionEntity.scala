package com.radovan.play.entity

import jakarta.persistence.{Column, Entity, GeneratedValue, GenerationType, Id, Table}

import java.sql.Timestamp
import scala.beans.BeanProperty

@SerialVersionUID(1L)
@Entity
@Table(name="infractions")
class InfractionEntity extends Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty var id:java.lang.Long = _

  @Column(nullable = false,name="infraction_time")
  @BeanProperty var infractionTime:Timestamp = _

  @Column(name="vehicle_registration_number",nullable = false,length = 25)
  @BeanProperty var vehicleRegistrationNumber:String = _

  @Column(name="vehicle_speed",nullable = false)
  @BeanProperty var vehicleSpeed:java.lang.Integer = _

  @Column(name="radar_id",nullable = false)
  @BeanProperty var radarId:java.lang.Long = _

  @Column(name="infraction_amount",nullable = false)
  @BeanProperty var infractionAmount:java.lang.Long = _

}
