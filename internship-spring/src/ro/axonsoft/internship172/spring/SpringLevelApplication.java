package ro.axonsoft.internship172.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import ro.axonsoft.internship172.configuration.ApplicationConfiguration;
import ro.axonsoft.internship172.configuration.BusinessConfiguration;
import ro.axonsoft.internship172.data.DataConfiguration;

@SpringBootApplication
@Import({ DataConfiguration.class, BusinessConfiguration.class, ApplicationConfiguration.class })
public class SpringLevelApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringLevelApplication.class, args);
    }

}