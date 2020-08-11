package com.rabobank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.access.servlet.TeeFilter;

@Configuration
public class FilterConfiguration {
	@Autowired
	@Bean
	public FilterRegistrationBean<TeeFilter> requestResponseFilter() {
		final FilterRegistrationBean<TeeFilter> filterRegBean = new FilterRegistrationBean<TeeFilter>();
		TeeFilter filter = new TeeFilter();
		filterRegBean.setFilter(filter);
		filterRegBean.addUrlPatterns("/api/record");
		filterRegBean.setName("Request Response Filter");
		filterRegBean.setAsyncSupported(Boolean.TRUE);
		return filterRegBean;
	}
}
