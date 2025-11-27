package com.radovan.scalatra.utils

import com.radovan.scalatra.services.EurekaServiceDiscovery
import jakarta.inject.Inject

import scala.collection.concurrent.TrieMap

class ServiceUrlProvider @Inject() (eurekaServiceDiscovery: EurekaServiceDiscovery) {

  private val cachedServiceUrls = TrieMap.empty[String, String]

  def getServiceUrl(serviceName: String): String = {
    cachedServiceUrls.getOrElseUpdate(serviceName, {
      val url = eurekaServiceDiscovery.getServiceUrl(serviceName)
      validateUrl(url, serviceName)
      url
    })
  }

  def getRadarServiceUrl: String = getServiceUrl("radar-service")
  def getRegistrationServiceUrl: String = getServiceUrl("registration-service")
  def getInfractionServiceUrl: String = getServiceUrl("infraction-service")
  def getApiGatewayUrl: String = getServiceUrl("api-gateway")
  def getAuthServiceUrl:String = getServiceUrl("auth-service")

  private def validateUrl(url: String, serviceName: String): Unit = {
    if (url == null || !url.startsWith("http")) {
      throw new IllegalArgumentException(s"Invalid URL for $serviceName: $url")
    }
  }
}