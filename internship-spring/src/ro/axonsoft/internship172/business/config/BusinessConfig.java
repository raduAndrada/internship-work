package ro.axonsoft.internship172.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.axonsoft.internship172.business.impl.base.SystemClockTimeManager;
import ro.axonsoft.internship172.business.impl.base.TimeManager;

@Configuration
public class BusinessConfig {

	@Bean
	public TimeManager timeManager() {
		return new SystemClockTimeManager();
	}
}
