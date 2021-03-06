package ro.axonsoft.internship172.data.tests;

import java.time.Instant;
import java.util.Deque;
import java.util.TimeZone;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;
import com.google.common.collect.Lists;

import ro.axonsoft.internship172.business.impl.base.TimeManager;
import ro.axonsoft.internship172.rest.configuration.BusinessConfig;
import ro.axonsoft.internship172.rest.configuration.BusinessConfigurationForRest;
import ro.axonsoft.internship172.rest.configuration.DataConfiguration;
import ro.axonsoft.internship172.rest.configuration.WebConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RecrutareRestTests.Config.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@ActiveProfiles("test")
@Ignore
public class RecrutareRestTests {

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

	public <T> HttpEntity<T> createRqWithAuth(final T payload, final String user) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("X-Authorization", user);
		return new HttpEntity<>(payload, headers);
	}

	@SpringBootApplication
	@Import({ DataConfiguration.class, BusinessConfig.class, BusinessConfigurationForRest.class,
			WebConfiguration.class })
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
