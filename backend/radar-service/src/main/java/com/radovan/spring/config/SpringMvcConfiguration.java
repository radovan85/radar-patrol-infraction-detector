package com.radovan.spring.config;

import com.radovan.spring.utils.HibernateUtil;
import com.radovan.spring.utils.NatsUtils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import tools.jackson.databind.ObjectMapper;

import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.radovan.spring")
@EnableScheduling
public class SpringMvcConfiguration implements WebMvcConfigurer {

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
	public HibernateUtil hibernateUtil() {
		return new HibernateUtil();
	}

	@Bean
	public SessionFactory sessionFactory(HibernateUtil util) {
		return util.getSessionFactory();
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
}