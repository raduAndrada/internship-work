package ro.axonsoft.internship172.web.app;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

	@Bean
	public RestTemplate restTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add((request, body, execution) -> {
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			final Optional<User> user = Optional.ofNullable(authentication).map(Authentication::getPrincipal)
					.map(User.class::cast);
			if (user.isPresent()) {
				request.getHeaders().add("X-Authorization", user.get().getUsername());
			}
			return execution.execute(request, body);
		});
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