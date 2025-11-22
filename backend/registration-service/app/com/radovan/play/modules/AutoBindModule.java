package com.radovan.play.modules;

import com.google.inject.AbstractModule;
import com.radovan.play.brokers.NatsBrokerListener;
import com.radovan.play.converter.TempConverter;
import com.radovan.play.repositories.OwnerRepository;
import com.radovan.play.repositories.VehicleRepository;
import com.radovan.play.repositories.impl.OwnerRepositoryImpl;
import com.radovan.play.repositories.impl.VehicleRepositoryImpl;
import com.radovan.play.services.*;
import com.radovan.play.services.impl.*;
import com.radovan.play.utils.JwtUtil;
import com.radovan.play.utils.LoadData;
import com.radovan.play.utils.PublicKeyCache;
import com.radovan.play.utils.ServiceUrlProvider;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

public class AutoBindModule extends AbstractModule {

    @Override
    protected void configure(){
        bind(OwnerService.class).to(OwnerServiceImpl.class).asEagerSingleton();
        bind(VehicleService.class).to(VehicleServiceImpl.class).asEagerSingleton();
        bind(PrometheusService.class).to(PrometheusServiceImpl.class).asEagerSingleton();
        bind(EurekaServiceDiscovery.class).to(EurekaServiceDiscoveryImpl.class).asEagerSingleton();
        bind(EurekaRegistrationService.class).to(EurekaRegistrationServiceImpl.class).asEagerSingleton();


        bind(OwnerRepository.class).to(OwnerRepositoryImpl.class).asEagerSingleton();
        bind(VehicleRepository.class).to(VehicleRepositoryImpl.class).asEagerSingleton();


        bind(JwtUtil.class).asEagerSingleton();
        bind(PublicKeyCache.class).asEagerSingleton();
        bind(ServiceUrlProvider.class).asEagerSingleton();
        bind(LoadData.class).asEagerSingleton();
        bind(TempConverter.class).asEagerSingleton();
        bind(NatsBrokerListener.class).asEagerSingleton();
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        bind(PrometheusMeterRegistry.class).toInstance(prometheusRegistry);
        bind(MeterRegistry.class).toInstance(prometheusRegistry);
    }
}
