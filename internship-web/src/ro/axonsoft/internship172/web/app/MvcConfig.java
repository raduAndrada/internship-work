package ro.axonsoft.internship172.web.app;

import java.util.List;
import java.util.Locale;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ro.axonsoft.internship172.web.util.RestServiceResponseErrorHandler;

/**
 * Clasa de configurare pentru modulul de web
 *
 * @author Andra
 *
 */
@Configuration
@ComponentScan({ "ro.axonsoft.internship172.web.controllers", "ro.axonsoft.internship172.web.util" })
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		registry.addInterceptor(localeChangeInterceptor);
	}

	/**
	 * Bean pentru fisiere
	 *
	 * @return
	 */
	@Bean
	MultipartConfigElement multipartConfigElement() {
		final MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("512KB");
		factory.setMaxRequestSize("512KB");
		return factory.createMultipartConfig();
	}

	/**
	 * Bean pentru url-ul curent
	 *
	 * @return
	 */
	@Bean
	public Function<String, String> currentUrlWithoutParam() {
		return param -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam(param).toUriString();
	}

	/**
	 *
	 * @return functionare in mai multe limbi - default en
	 */
	@Bean
	public SessionLocaleResolver localeResolver() {
		final SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en", "US"));
		return localeResolver;
	}

	@Autowired
	private MessageSource messageSource;

	@Override
	public Validator getValidator() {
		final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource);
		return validator;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

		final MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
		final ObjectMapper objectMapper = jacksonMessageConverter.getObjectMapper();

		objectMapper.registerModule(new GuavaModule());
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

		converters.add(jacksonMessageConverter);
	}

	@Bean
	public RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		// restTemplate.getInterceptors().add((request, body, execution) -> {
		// final User user = (User)
		// SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// if (user != null) {
		// request.getHeaders().add("X-Authorization", user.getUsername());
		// }
		// return execution.execute(request, body);
		// });
		restTemplate.setErrorHandler(restServiceResponseErrorHandler());
		return restTemplate;
	}

	@Bean
	public RestServiceResponseErrorHandler restServiceResponseErrorHandler() {
		return new RestServiceResponseErrorHandler();
	}

	@Override
	public void configurePathMatch(final PathMatchConfigurer configurer) {
		configurer.setUseRegisteredSuffixPatternMatch(true);
	}

}