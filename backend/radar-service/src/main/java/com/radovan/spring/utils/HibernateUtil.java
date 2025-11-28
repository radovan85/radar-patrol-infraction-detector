package com.radovan.spring.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PreDestroy;

public class HibernateUtil {

	private final SessionFactory sessionFactory;
	private final HikariDataSource hikariDataSource;

	public HibernateUtil() {
		HikariConfig hikariConfig = new HikariConfig();
		String persistenceUrl = System.getenv("DB_URL");
		String persistencePassword = System.getenv("DB_PASSWORD");
		String persistenceUsername = System.getenv("DB_USERNAME");
		if (persistenceUsername == null || persistencePassword == null || persistenceUrl == null)
			throw new IllegalStateException("Database environment variables are missing!");
		hikariConfig.setJdbcUrl(persistenceUrl);
		hikariConfig.setUsername(persistenceUsername);
		hikariConfig.setPassword(persistencePassword);
		hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
		hikariConfig.setMinimumIdle(2);
		hikariConfig.setIdleTimeout(600000);
		hikariConfig.setMaximumPoolSize(10);
		hikariConfig.setConnectionTimeout(30000);
		hikariConfig.setMaxLifetime(1800000);

		this.hikariDataSource = new HikariDataSource(hikariConfig);

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
				.applySetting("hibernate.hbm2ddl.auto", "update")
				.applySetting("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
				.applySetting("hibernate.connection.datasource", hikariDataSource)
				.applySetting("hibernate.show_sql", "false").applySetting("hibernate.format_sql", "false").build();

		this.sessionFactory = new MetadataSources(serviceRegistry)
				.addAnnotatedClass(com.radovan.spring.entity.RadarEntity.class).buildMetadata().buildSessionFactory();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@PreDestroy
	public void shutdown() {
		System.out.println("Shutting down HibernateUtil...");
		if (sessionFactory != null) {
			sessionFactory.close();
		}
		if (hikariDataSource != null) {
			hikariDataSource.close();
		}
	}
}
