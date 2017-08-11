package ro.axonsoft.internship172.web.app;

import java.util.Locale;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ro.axonsoft.internship172.web.services.ResultRestService;
import ro.axonsoft.internship172.web.services.ResultRestServiceImpl;
import ro.axonsoft.internship172.web.services.VehicleOwnerRestService;
import ro.axonsoft.internship172.web.services.VehicleOwnerRestServiceImpl;

/**
 * Clasa de configurare pentru modulul de web
 *
 * @author Andra
 *
 */
@Configuration
@ComponentScan({
    "ro.axonsoft.internship172.web.controllers",
    "ro.axonsoft.internship172.web.services"
})
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }

    /**
     * Bean pentru fisiere
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

    /**
     *
     * @return serviciile rest pentru tabela de intrare
     */
    @Bean
    public VehicleOwnerRestService vehicleOwnerRestService() {
        return new VehicleOwnerRestServiceImpl();
    }

    /**
     *
     * @return serviciu rest pentru rezultate
     */
    @Bean
    public ResultRestService resultRestService() {
        return new ResultRestServiceImpl();
    }

}