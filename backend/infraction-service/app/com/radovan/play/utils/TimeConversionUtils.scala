package com.radovan.play.utils

import jakarta.inject.Singleton
import java.sql.Timestamp
import java.time._
import java.time.format.{DateTimeFormatter, DateTimeParseException}

@Singleton
class TimeConversionUtils {

  private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
  private val zoneId: ZoneId = ZoneId.of("UTC")


  def getCurrentUTCTimestamp: Timestamp = {
    val currentTime = Instant.now().atZone(zoneId)
    Timestamp.valueOf(currentTime.toLocalDateTime)
  }



  def stringToTimestamp(str: String): Timestamp = {
    try {
      val zoned = LocalDateTime.parse(str, formatter).atZone(zoneId)
      Timestamp.valueOf(zoned.toLocalDateTime)
    } catch {
      case ex: DateTimeParseException =>
        println(s"❌ ERROR parsing to timestamp: ${ex.getMessage}")
        null
    }
  }

  def timestampToString(timestamp: Timestamp): String = {
    val utcTime = timestamp.toLocalDateTime.atZone(zoneId)
    utcTime.format(formatter)
  }
}
