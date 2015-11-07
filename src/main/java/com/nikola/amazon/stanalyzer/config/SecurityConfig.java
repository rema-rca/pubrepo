package com.nikola.amazon.stanalyzer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@PropertySource({ "classpath:stanalyzer-security.properties" })
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment env;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication()
				.withUser(env.getProperty("security_user"))
				.password(env.getProperty("security_user_password"))
				.roles("USER, ADMIN");
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/static/**", "/login*", "/logout*").permitAll()
				.antMatchers("/**").hasRole("ADMIN").anyRequest()
				.authenticated()

				.and().formLogin().loginPage("/login.html")
				.failureUrl("/login.html?error")
				.usernameParameter("username").passwordParameter("password")
				.defaultSuccessUrl("/index.html").and().logout()
				.deleteCookies("JSESSIONID").logoutUrl("/logout")
				.logoutSuccessUrl("/login.html?logout").and()
				.sessionManagement().maximumSessions(1);

	}

}
