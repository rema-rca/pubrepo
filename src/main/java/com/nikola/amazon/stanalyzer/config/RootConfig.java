package com.nikola.amazon.stanalyzer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.nikola.amazon.stanalyzer.config.JmxConfig;

@Configuration
@Import({PersistenceConfig.class, SecurityConfig.class, SchedulerConfig.class, MailConfig.class, JmxConfig.class})
public class RootConfig {
	


}
