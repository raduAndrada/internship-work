package ro.axonsoft.internshi.business.impl.result;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonsoft.internship172.data.api.result.MdfResultEntity;
import ro.axonsoft.internship172.data.api.result.ResultEntity;
import ro.axonsoft.internship172.data.tests.RecrutareBusinessTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.base.ImtBatch;
import ro.axonsoft.internship172.model.base.MdfBatch;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.result.ImtResultBasic;
import ro.axonsoft.internship172.model.result.ImtResultCreate;
import ro.axonsoft.internship172.model.result.ImtResultGet;
import ro.axonsoft.internship172.model.result.ImtResultMetricsCreateResult;
import ro.axonsoft.internship172.model.result.ImtResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ImtResultRecord;
import ro.axonsoft.internship172.model.result.ImtResultSortCriterion;
import ro.axonsoft.internship172.model.result.MdfResultBasic;
import ro.axonsoft.internship172.model.result.MdfResultErrorBasic;
import ro.axonsoft.internship172.model.result.MdfResultErrorRecord;
import ro.axonsoft.internship172.model.result.MdfResultRecord;
import ro.axonsoft.internship172.model.result.MdfResultUnregCarsCountByJudBasic;
import ro.axonsoft.internship172.model.result.MdfResultUnregCarsCountByJudRecord;
import ro.axonsoft.internship172.model.result.ResultMetricsCreateResult;
import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultSortCriterionType;

public class ResultBusinessTests extends RecrutareBusinessTests {

	private static final String RES_TN = "RESULT_METRICS";
	private static final String UNREG_CARS_TN = "RESULT_UNREG_CARS_COUNT_BY_JUD";
	private static final String ERR_TN = "RESULT_ERROR";

	private static final String RES_ID_CLM_NAME = "RESULT_METRICS_ID";
	private static final String UNREG_CARS_ID_CLM_NAME = "UNREG_cARS_COUNT_ID";
	private static final String ERR_ID_CLM_NAME = "RESULT_ERROR_ID";

	@Inject
	private ResultBusiness resBusiness;

	@Inject
	private TestDbUtil dbUtil;

	private static final ResultEntity RES1 = MdfResultEntity.create().setRecord(MdfResultRecord.create()
			.setBasic(MdfResultBasic.create().setOddToEvenRatio(100).setPassedRegChangeDueDate(5)
					.setResultProcessTime(Timestamp.from(Instant.parse("2017-08-02T11:47:00.00Z"))))
			.addError(MdfResultErrorRecord.create().setResultMetricsId(6L).setResultErrorId(20L)
					.setBasic(MdfResultErrorBasic.create().setType(1).setVehicleOwnerId(1L)))
			.addUnregCar(MdfResultUnregCarsCountByJudRecord.create().setResultMetricsId(6L).setUnregCarsCountId(20L)
					.setBasic(MdfResultUnregCarsCountByJudBasic.create().setJudet(Judet.AB).setUnregCarsCount(2)))
			.setBatch(MdfBatch.create().setBatchId(6L))).setResultMetricsId(6L);

	@Test
	@DatabaseSetup(value = "ResultBusinessTests-01-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "ResultBusinessTests-01-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testCreateResultOnEmptyDatabase() throws Exception {
		dbUtil.setIdentity(RES_TN, RES_ID_CLM_NAME, 6);
		dbUtil.setIdentity(UNREG_CARS_TN, UNREG_CARS_ID_CLM_NAME, 20);
		dbUtil.setIdentity(ERR_TN, ERR_ID_CLM_NAME, 20);
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final ResultMetricsCreateResult resCreateResult = resBusiness.createResult(ImtResultCreate.builder()
				.basic(RES1.getRecord().getBasic()).errors(RES1.getRecord().getErrors())
				.unregCars(RES1.getRecord().getUnregCars()).batch(ImtBatch.builder().batchId(6L).build()).build());
		assertThat(resCreateResult)
				.isEqualTo(ImtResultMetricsCreateResult.builder()
						.basic(ImtResultBasic.builder().oddToEvenRatio(100).passedRegChangeDueDate(5)
								.resultProcessTime(Timestamp.from(Instant.parse("2017-08-02T11:47:00.00Z"))).build())
						.build());

	}

	@Test
	@DatabaseSetup(value = "ResultBusinessTests-02-i.xml")
	public void testGetResult() throws Exception {
		dbUtil.setIdentity(RES_TN, RES_ID_CLM_NAME, 6);
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final ResultMetricsGetResult resGetResult = resBusiness.getResults(ImtResultGet.builder()
				.addSort(ImtResultSortCriterion.builder().criterion(ResultSortCriterionType.BATCH_ID)
						.direction(SortDirection.ASC).build())
				.batchId(0L).build());
		assertThat(resGetResult).isEqualTo(ImtResultMetricsGetResult.builder().count(1).pageCount(1)
				.addList(ImtResultRecord.builder()
						.basic(ImtResultBasic.builder().oddToEvenRatio(100).passedRegChangeDueDate(5)
								.resultProcessTime(Timestamp.from(Instant.parse("2017-08-02T11:47:00.00Z"))).build())
						.addError(MdfResultErrorRecord.create().setResultMetricsId(6L).setResultErrorId(0L)
								.setBasic(MdfResultErrorBasic.create().setType(1).setVehicleOwnerId(1L)))
						.addUnregCar(MdfResultUnregCarsCountByJudRecord.create().setResultMetricsId(6L)
								.setUnregCarsCountId(0L)
								.setBasic(MdfResultUnregCarsCountByJudBasic.create().setJudet(Judet.AB)
										.setUnregCarsCount(2)))
						.batch(ImtBatch.builder().batchId(0L).build()).build())
				.build());

	}
}
