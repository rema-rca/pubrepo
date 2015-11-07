package com.nikola.amazon.stanalyzer.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.nikola.amazon.stanalyzer.scheduler.AbstractStanFetchClient;
import com.nikola.amazon.stanalyzer.scheduler.HaloOglasiFetchClient;
import com.nikola.amazon.stanalyzer.scheduler.NekretnineFetchClient;

@Configuration
@Import({ PersistenceConfig.class })
@EnableScheduling
@ComponentScan(basePackages = { "com.nikola.amazon.stanalyzer.scheduler" })
@PropertySource({ "classpath:stanalyzer-scheduler.properties" })
public class SchedulerConfig implements AsyncConfigurer {

	@Autowired
	private Environment environment;

	@Override
	@Bean(destroyMethod = "shutdown")
	public Executor getAsyncExecutor() {
		return Executors.newFixedThreadPool(2);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public List<AbstractStanFetchClient> fetchClients() {
		List<AbstractStanFetchClient> fetchClients = new ArrayList<>();
		fetchClients.add(nekretnineFetchClient());
		fetchClients.add(haloOglasiFetchClient());
		return fetchClients;
	}

	@Bean
	public AbstractStanFetchClient haloOglasiFetchClient() {
		HaloOglasiFetchClient client = new HaloOglasiFetchClient();
		client.setUrl(getClientUrl(HaloOglasiFetchClient.class));
		return client;
	}

	@Bean
	public AbstractStanFetchClient nekretnineFetchClient() {
		NekretnineFetchClient client = new NekretnineFetchClient();
		client.setUrl(getClientUrl(NekretnineFetchClient.class));
		return client;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	private String getClientUrl(Class<?> clientClazz) {
		return environment.getProperty(clientClazz.getSimpleName()
				.toLowerCase());
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {
			@Override
			public void handleUncaughtException(Throwable ex, Method method,
					Object... params) {
				System.out.println("Exception " + ex + " invoking "
						+ method.getName() + " with arguments "
						+ Arrays.toString(params));
			}
		};
	}

}
