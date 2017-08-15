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

import ro.axonsoft.internship172.api.Judet;
import ro.axonsoft.internship172.data.tests.RecrutareDataTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.MdfBatch;
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

	private static final ResultEntity RES1 = MdfResultEntity.create()
			.setRecord(MdfResultRecord.create()
					.setBasic(MdfResultBasic.create().setOddToEvenRatio(100).setPassedRegChangeDueDate(5)
							.setResultProcessTime(Timestamp.from(Instant.parse("2017-08-02T11:47:00.00Z"))))
					.setBatch(MdfBatch.create().setBatchId(0L)))
			.setResultMetricsId(6L)

			.addErrors(MdfResultErrorRecord.create().setResultMetricsId(6L).setResultErrorId(0L)
					.setBasic(MdfResultErrorBasic.create().setType(1).setVehicleOwnerId(1L)))
			.addUnregCars(MdfResultUnregCarsCountByJudRecord.create().setResultMetricsId(6L).setUnregCarsCountId(0L)
					.setBasic(MdfResultUnregCarsCountByJudBasic.create().setJudet(Judet.AB).setUnregCarsCount(2)));
	//
	// private static final VehicleOwnerEntity VHO2 =
	// MdfVehicleOwnerEntity.create().setVehicleOwnerId(1L)
	// .setRecord(MdfVehicleOwnerBasicRecord.create()
	// .setBasic(MdfVehicleOwnerBasic.create().setRoIdCard("KX636142").setComentariu("000")
	// .setIssueDate(Instant.parse("2017-08-02T11:47:00.00Z")).setRegPlate("CJ84ADD"))
	// .setBatch(MdfBatch.create().setBatchId(0L)));
	//
	// private static final Batch BATCH1 = MdfBatch.create().setBatchId(6L);
	// private static final Batch BATCH2 = MdfBatch.create().setBatchId(7L);
	// private static final Batch BATCH3 = MdfBatch.create().setBatchId(8L);

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

		dbUtil.setIdentity("RESULT_UNREG_CARS_COUNT_BY_JUD", "UNREG_CARS_COUNT_ID", 0);
		resultMetricsDao.addUnregCars(RES1.getUnregCars());

		dbUtil.setIdentity("RESULT_ERROR", "RESULT_ERROR_ID", 0);
		resultMetricsDao.addErrors(RES1.getErrors());

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

	// @Test
	// @DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	// public void testGetVehicleOwner_ByRoIdCard_orderCrtTmsAsc() {
	// assertThat(vehicleOwnerDao.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
	// .criteria(ImtVehicleOwnerEntityCriteria.builder().idBatchSelect(ImmutableList.of(0L)).build())
	// .sort(ImmutableList.of(ImtVehicleOwnerSortCriterion.builder()
	// .criterion(VehicleOwnerSortCriterionType.RO_ID_CARD).direction(SortDirection.ASC).build()))
	// .pagination(ImtPagination.of(1,
	// 1)).search("KX636142").build())).isEqualTo(ImmutableList.of(VHO2));
	// }
	//
	// @Test
	// @DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	// @ExpectedDatabase(value = "VehicleOwnerDaoTests-02-e.xml", assertionMode =
	// DatabaseAssertionMode.NON_STRICT_UNORDERED)
	// public void testDeleteVehicleOwner_oneDelete() {
	//
	// vehicleOwnerDao.deleteVehicleOwner(ImtVehicleOwnerEntityDelete.builder()
	// .criteria(ImtVehicleOwnerEntityCriteria.builder().addRoIdCardIncl("KX636142").build()).build());
	//
	// }
	//
	// @Test
	// @DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	// @ExpectedDatabase(value = "VehicleOwnerDaoTests-03-e.xml", assertionMode =
	// DatabaseAssertionMode.NON_STRICT_UNORDERED)
	// public void testUpdateVehicleOwner_oneUpdate() {
	//
	// vehicleOwnerDao.updateVehicleOwner(ImtVehicleOwnerEntityUpdate.builder()
	// .criteria(ImtVehicleOwnerEntityCriteria.builder().addIdIncl(1L).build()).comentariu("asta")
	// .regPlate("abc1209s").build());
	//
	// }

}