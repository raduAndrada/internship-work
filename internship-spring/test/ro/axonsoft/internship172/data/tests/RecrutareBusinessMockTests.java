package ro.axonsoft.internship172.data.tests;

import java.time.Instant;
import java.util.Deque;
import java.util.TimeZone;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.business.config.BusinessConfig;
import ro.axonsoft.internship172.business.impl.base.TimeManager;
import ro.axonsoft.internship172.configuration.VehicleOwnerBusinessConfiguration;
import ro.axonsoft.internship172.data.DataConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RecrutareBusinessMockTests.Config.class)
@Ignore
public class RecrutareBusinessMockTests {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Inject
	private Config config;

	public void pushInstant(final Instant instant) {
		config.instantsQueue.push(instant);
	}

	@Before
	public void reset() {
		config.instantsQueue.clear();

	}

	@SpringBootApplication
	@Import({ DataConfiguration.class, BusinessConfig.class, VehicleOwnerBusinessConfiguration.class })
	public static class Config extends RecrutareTestsConfigBase {

		private final Deque<Instant> instantsQueue = Lists.newLinkedList();

		@Bean
		public TimeManager timeManager() {
			return () -> instantsQueue.pop();
		}

		public void pushInstant(final Instant instant) {
			instantsQueue.push(instant);
		}

	}
}
