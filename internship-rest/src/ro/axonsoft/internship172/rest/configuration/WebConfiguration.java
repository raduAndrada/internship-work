package ro.axonsoft.internship172.rest.configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ro.axonsoft.internship172.business.impl.base.CurrentUserContext;
import ro.axonsoft.recrutare.rest.security.CurrentUserContextImpl;
import ro.axonsoft.recrutare.rest.security.SecurityHandlerInterceptor;

@Configuration
@ComponentScan("ro.axonsoft.recrutare.rest")
public class WebConfiguration extends WebMvcConfigurerAdapter {

	@Inject
	private ObjectMapper objectMapper;

	@Override
	public void configurePathMatch(final PathMatchConfigurer configurer) {
		configurer.setUseRegisteredSuffixPatternMatch(true);
	}

	@PostConstruct
	public void setupObjectMapper() {
		objectMapper.registerModule(new GuavaModule());
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Bean
	public CurrentUserContext currentUserContext() {
		return new CurrentUserContextImpl();
	}

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new SecurityHandlerInterceptor());
	}
}
