package ro.axonsoft.internship172.data.api.result;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.TimeZone;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.data.tests.RecrutareDataTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.MdfResultBatch;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.result.ImtResultSortCriterion;
import ro.axonsoft.internship172.model.result.MdfResultBasic;
import ro.axonsoft.internship172.model.result.MdfResultErrorBasic;
import ro.axonsoft.internship172.model.result.MdfResultErrorRecord;
import ro.axonsoft.internship172.model.result.MdfResultRecord;
import ro.axonsoft.internship172.model.result.MdfResultUnregCarsCountByJudBasic;
import ro.axonsoft.internship172.model.result.MdfResultUnregCarsCountByJudRecord;
import ro.axonsoft.internship172.model.result.ResultSortCriterionType;

public class ResultMetricsDaoTests extends RecrutareDataTests {

	private static final ResultEntity RES1 = MdfResultEntity.create().setRecord(MdfResultRecord.create()
			.setBasic(MdfResultBasic.create().setOddToEvenRatio(100).setPassedRegChangeDueDate(5)
					.setResultProcessTime(Timestamp.from(Instant.parse("2017-08-02T11:47:00.00Z"))))
			.setBatch(MdfResultBatch.create().setBatchId(0L))
			.addError(MdfResultErrorRecord.create().setResultMetricsId(6L).setResultErrorId(0L)
					.setBasic(MdfResultErrorBasic.create().setType(1).setVehicleOwnerId(1L)))
			.addUnregCar(MdfResultUnregCarsCountByJudRecord.create().setResultMetricsId(6L).setUnregCarsCountId(0L)
					.setBasic(MdfResultUnregCarsCountByJudBasic.create().setJudet(Judet.AB).setUnregCarsCount(2))))
			.setResultMetricsId(6L);

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Inject
	private ResultMetricsDao resultMetricsDao;

	@Inject
	private TestDbUtil dbUtil;

	@Test
	@DatabaseSetup(value = "ResultMetricsDaoTests-01-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "ResultMetricsDaoTests-01-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testAddOneResultWithErrorAndUnregCars_OnEmptyDatabase() throws Exception {
		dbUtil.setIdentity("RESULT_METRICS", "RESULT_METRICS_ID", 6);
		resultMetricsDao.addResult(MdfResultEntity.create().from(RES1).setResultMetricsId(null));

		dbUtil.setIdentity("RESULT_UNREG_CARS_COUNT_BY_JUD", "UNREG_CARS_COUNT_ID", 20);
		resultMetricsDao.addUnregCars(RES1.getRecord().getUnregCars());

		dbUtil.setIdentity("RESULT_ERROR", "RESULT_ERROR_ID", 20);
		resultMetricsDao.addErrors(RES1.getRecord().getErrors());

	}

	@Test
	@DatabaseSetup("ResultMetricsDaoTests-03-i.xml")
	public void testGetResult() {
		assertThat(resultMetricsDao.getResult(ImtResultEntityGet.builder()
				.sort(ImmutableList.of(ImtResultSortCriterion.builder()
						.criterion(ResultSortCriterionType.RESULT_PROCESS_TIME).build()))
				.build())).isEqualTo(ImmutableList.of(RES1));

	}

	@Test
	@DatabaseSetup("ResultMetricsDaoTests-03-i.xml")
	public void testGetResult_difPag_orderCrtTmsAsc_BatchSelect() {
		assertThat(resultMetricsDao.getResult(ImtResultEntityGet.builder()
				.criteria(ImtResultEntityCriteria.builder().addIdBatchSelect(0L).build())
				.pagination(ImtPagination.of(1, 1)).addSort(ImtResultSortCriterion.builder()
						.criterion(ResultSortCriterionType.RESULT_PROCESS_TIME).direction(SortDirection.ASC).build())
				.build())).isEqualTo(ImmutableList.of(RES1));

	}

	@Test
	@DatabaseSetup("ResultMetricsDaoTests-02-i.xml")
	@ExpectedDatabase(value = "ResultMetricsDaoTests-02-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testDeleteResult_oneDelete_inEveryResultTable() {

		resultMetricsDao.deleteResult(ImtResultEntityDelete.builder()
				.criteria(ImtResultEntityCriteria.builder().addIdIncl(1L).build()).build());
		resultMetricsDao.deleteResultError(ImtResultErrorEntityDelete.builder()
				.criteria(ImtChildTableCriteria.builder().addIdResultSelect(1L).build()).build());

	}

	@Test
	@DatabaseSetup("ResultMetricsDaoTests-03-i.xml")
	@ExpectedDatabase(value = "ResultMetricsDaoTests-03-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testUpdateResult_oneUpdate() {

		resultMetricsDao.updateResult(
				ImtResultEntityUpdate.builder().criteria(ImtResultEntityCriteria.builder().addIdIncl(6L).build())
						.oddToEvenRatio(50).passedRegChangeDueDate(1).build());

	}

}