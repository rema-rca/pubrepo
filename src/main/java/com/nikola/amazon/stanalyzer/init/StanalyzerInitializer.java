package com.nikola.amazon.stanalyzer.init;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.nikola.amazon.stanalyzer.config.MvcConfig;
import com.nikola.amazon.stanalyzer.config.RootConfig;

public class StanalyzerInitializer  extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[]{RootConfig.class};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{MvcConfig.class};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}
	
	@Override
    protected void customizeRegistration(Dynamic registration) {
        // additional configuration, here for MultipartConfig
        super.customizeRegistration(registration);
        MultipartConfigElement multipartConf = new MultipartConfigElement("", 200000L, -1L, 0);
        registration.setMultipartConfig(multipartConf);
    }

}
