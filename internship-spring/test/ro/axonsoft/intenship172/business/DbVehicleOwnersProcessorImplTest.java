package ro.axonsoft.intenship172.business;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import ro.axonsoft.internship172.data.tests.RecrutareBusinessTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.api.DbVehicleOwnersProcessor;

public class DbVehicleOwnersProcessorImplTest extends RecrutareBusinessTests {

	private static final String RES_TN = "RESULT_METRICS";
	private static final String ERR_TN = "RESULT_ERROR";
	private static final String CARS_TN = "RESULT_UNREG_CARS_COUNT_BY_JUD";

	private static final String RES_ID_CLM_NAME = "RESULT_METRICS_ID";
	private static final String ERR_ID_CLM_NAME = "RESULT_ERROR_ID";
	private static final String CARS_ID_CLM_NAME = "UNREG_CARS_COUNT_ID";

	@Inject
	private TestDbUtil dbUtil;
	@Inject
	private DbVehicleOwnersProcessor processor;

	@Test
	@DatabaseSetup("DbVehicleOwnersProcessorImplTest-i01.xml")
	@ExpectedDatabase(value = "DbVehicleOwnersProcessorImplTest-e01.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void processComplexTest() {
		dbUtil.setIdentity(RES_TN, RES_ID_CLM_NAME, 6);
		dbUtil.setIdentity(ERR_TN, ERR_ID_CLM_NAME, 6);
		dbUtil.setIdentity(CARS_TN, CARS_ID_CLM_NAME, 6);
		processor.process(100L);

	}

	// @Test
	// @DatabaseSetup("DbVehicleOwnersProcessorImplTest-i01.xml")
	// @ExpectedDatabase(value = "DbVehicleOwnersProcessorImplTest-e02.xml",
	// assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	// public void processSimpleTest() {
	// dbUtil.setIdentity(RES_TN, RES_ID_CLM_NAME, 10);
	// dbUtil.setIdentity(ERR_TN, ERR_ID_CLM_NAME, 10);
	// dbUtil.setIdentity(CARS_TN, CARS_ID_CLM_NAME, 10);
	// processor.process(3L);
	//
	// }
}
