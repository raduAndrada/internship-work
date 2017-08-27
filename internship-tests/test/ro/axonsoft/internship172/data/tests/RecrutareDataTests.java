package ro.axonsoft.internship172.data.tests;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;

import ro.axonsoft.internship172.rest.configuration.DataConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { RecrutareDataTests.Config.class }, webEnvironment = WebEnvironment.NONE)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@ActiveProfiles("test")
@Ignore
public class RecrutareDataTests {

	@SpringBootApplication
	@Import(DataConfiguration.class)
	public static class Config extends RecrutareTestsConfigBase {

	}

}
