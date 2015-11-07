package com.nikola.amazon.stanalyzer.config;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.nikola.amazon.stanalyzer.util.HibernateStatisticsFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:stanalyzer-database.properties" })
@ComponentScan({ "com.nikola.amazon.stanalyzer.dao" })
public class PersistenceConfig {

	@Autowired
	private Environment env;

	@Bean
	public com.zaxxer.hikari.HikariConfig hikariConfig() {
		com.zaxxer.hikari.HikariConfig config = new HikariConfig();
		config.setPoolName("springHikariCP");
		config.setDataSourceClassName(env
				.getProperty("dataSourceDriverClassName"));
		config.setMaximumPoolSize(3);
		config.setIdleTimeout(30000);

		Properties dataSourceProperties = new Properties();
		dataSourceProperties.setProperty("url", env.getProperty("databaseUrl"));
		dataSourceProperties.setProperty("user",
				env.getProperty("databaseUser"));
		dataSourceProperties.setProperty("password",
				env.getProperty("databasePassword"));
		config.setDataSourceProperties(dataSourceProperties);
		return config;
	}

	@Bean(destroyMethod = "close")
	public com.zaxxer.hikari.HikariDataSource dataSource() {
		com.zaxxer.hikari.HikariDataSource dataSource = new HikariDataSource(
				hikariConfig());
		dataSource.setPoolName("springHikariCP");
		dataSource.setRegisterMbeans(true);
		return dataSource;
	}

	@Bean
	public org.springframework.orm.hibernate4.LocalSessionFactoryBean sessionFactory() {
		org.springframework.orm.hibernate4.LocalSessionFactoryBean sessionFactory = new org.springframework.orm.hibernate4.LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(env.getProperty("databasePackagesToScan"));
		Properties sessionProperties = new Properties();
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_1"),
				env.getProperty("persistence_configuration_1_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_2"),
				env.getProperty("persistence_configuration_2_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_3"),
				env.getProperty("persistence_configuration_3_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_4"),
				env.getProperty("persistence_configuration_4_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_5"),
				env.getProperty("persistence_configuration_5_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_6"),
				env.getProperty("persistence_configuration_6_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_7"),
				env.getProperty("persistence_configuration_7_value"));
		sessionProperties.setProperty(
				env.getProperty("persistence_configuration_8"),
				env.getProperty("persistence_configuration_8_value"));
		sessionFactory.setHibernateProperties(sessionProperties);
		return sessionFactory;
	}

	@Bean
	public HibernateTransactionManager transactionManager(
			SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);

		return txManager;
	}
	
	@Bean
	public HibernateStatisticsFactoryBean hibernateStatisticsMBean() {
		HibernateStatisticsFactoryBean stat = new HibernateStatisticsFactoryBean();
		stat.setSessionFactory(sessionFactory().getObject());
		return stat;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
