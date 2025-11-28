package com.radovan.spring.config;

import com.radovan.spring.interceptors.AuthInterceptor;
import com.radovan.spring.interceptors.UnifiedMetricsInterceptor;
import com.radovan.spring.utils.NatsUtils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import tools.jackson.databind.ObjectMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.radovan.spring")
@EnableScheduling
public class SpringMvcConfiguration implements WebMvcConfigurer {

	private final ObjectProvider<AuthInterceptor> authInterceptorProvider;
	private final ObjectProvider<UnifiedMetricsInterceptor> metricsInterceptorProvider;

	public SpringMvcConfiguration(ObjectProvider<AuthInterceptor> authInterceptorProvider,
			ObjectProvider<UnifiedMetricsInterceptor> metricsInterceptorProvider) {
		this.authInterceptorProvider = authInterceptorProvider;
		this.metricsInterceptorProvider = metricsInterceptorProvider;
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public ModelMapper getMapper() {
		ModelMapper returnValue = new ModelMapper();
		returnValue.getConfiguration().setAmbiguityIgnored(true)
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
				.setMatchingStrategy(MatchingStrategies.STRICT);
		return returnValue;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public NatsUtils getNatsUtils() {
		return new NatsUtils();
	}

	@Bean
	@Primary
	public PrometheusMeterRegistry prometheusMeterRegistry() {
		return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
	}

	@Bean
	public MeterRegistry meterRegistry(PrometheusMeterRegistry prometheusRegistry) {
		return prometheusRegistry;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		AuthInterceptor authInterceptor = authInterceptorProvider.getIfAvailable();
		UnifiedMetricsInterceptor metricsInterceptor = metricsInterceptorProvider.getIfAvailable();

		if (authInterceptor != null) {
			registry.addInterceptor(authInterceptor).excludePathPatterns("/prometheus");
		}
		if (metricsInterceptor != null) {
			registry.addInterceptor(metricsInterceptor).excludePathPatterns("/prometheus", "/api/health");
		}
	}
}
