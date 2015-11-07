package com.nikola.amazon.stanalyzer.config;

import java.util.HashMap;
import java.util.Map;

import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.spring.SpringJolokiaAgent;
import org.jolokia.jvmagent.spring.SpringJolokiaConfigHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

import com.nikola.amazon.stanalyzer.util.HibernateStatisticsFactoryBean;

@Configuration
public class JmxConfig {
	
	@Autowired
	private HibernateStatisticsFactoryBean hibernateStatisticsMBean;
	
	@Bean(destroyMethod="destroy")
	public JolokiaServer jolokiaServer() {
		SpringJolokiaAgent server = new SpringJolokiaAgent();
		server.setId("stanalyzerJolokiaServer");
		server.setLookupConfig(true);
		SpringJolokiaConfigHolder config = new SpringJolokiaConfigHolder();
		Map<String,String> configMap = new HashMap<>();
		configMap.put("autostart", "true");
		configMap.put("host", "127.0.0.1");
		configMap.put("port", "8778");
		config.setConfig(configMap);
		server.setConfig(config);
		return server;
	}
	
	@Bean
	public MBeanExporter exporter() {
		MBeanExporter exporter = new MBeanExporter();
		Map<String, Object> beans = new HashMap<>();
		beans.put("Hibernate:type=statistics", hibernateStatisticsMBean);
		exporter.setBeans(beans);
		return exporter;
	}

	public void setHibernateStatisticsMBean(
			HibernateStatisticsFactoryBean hibernateStatisticsMBean) {
		this.hibernateStatisticsMBean = hibernateStatisticsMBean;
	}
	
	

}
