package com.radovan.play.modules

import com.google.inject.AbstractModule
import com.radovan.play.brokers.{InfractionNatsListener, InfractionNatsSender}
import com.radovan.play.converter.TempConverter
import com.radovan.play.repository.InfractionRepository
import com.radovan.play.repository.impl.InfractionRepositoryImpl
import com.radovan.play.services.{EurekaRegistrationService, EurekaServiceDiscovery, InfractionService, PrometheusService}
import com.radovan.play.services.impl.{EurekaRegistrationServiceImpl, EurekaServiceDiscoveryImpl, InfractionServiceImpl, PrometheusServiceImpl}
import com.radovan.play.utils.{ServiceUrlProvider, TimeConversionUtils}
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.{PrometheusConfig, PrometheusMeterRegistry}

class AutoBindModule extends AbstractModule{

  override def configure():Unit = {
    bind(classOf[EurekaServiceDiscovery]).to(classOf[EurekaServiceDiscoveryImpl]).asEagerSingleton()
    bind(classOf[EurekaRegistrationService]).to(classOf[EurekaRegistrationServiceImpl]).asEagerSingleton()
    bind(classOf[InfractionService]).to(classOf[InfractionServiceImpl]).asEagerSingleton()
    bind(classOf[PrometheusService]).to(classOf[PrometheusServiceImpl]).asEagerSingleton()

    bind(classOf[InfractionRepository]).to(classOf[InfractionRepositoryImpl]).asEagerSingleton()

    bind(classOf[InfractionNatsSender]).asEagerSingleton()
    bind(classOf[InfractionNatsListener]).asEagerSingleton()
    bind(classOf[TimeConversionUtils]).asEagerSingleton()
    bind(classOf[TempConverter]).asEagerSingleton()
    bind(classOf[ServiceUrlProvider]).asEagerSingleton()

    val prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    bind(classOf[PrometheusMeterRegistry]).toInstance(prometheusRegistry)
    bind(classOf[MeterRegistry]).toInstance(prometheusRegistry)
  }

}
