package ro.axonsoft.internship172.web.app;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Configuration
public class ThymeleafConfig {

    @Bean
    public Function<String, String> currentUrlWithoutParam() {
        return param -> {
            final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
            Arrays.asList(param.split(",")).stream().map(String::trim).forEach(builder::replaceQueryParam);
            return builder.toUriString();
        };
    }

    @Bean
    public ZoneId zoneId() {
        return ZoneId.systemDefault();
    }
}
