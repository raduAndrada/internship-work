package ro.axonfost.internship172.business.impl.vehicleOwner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import ro.axonsoft.internship172.business.api.vehicleOwner.VehicleOwnerBusiness;
import ro.axonsoft.internship172.data.tests.RecrutareBusinessTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.base.ImtBatch;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.batch.BatchCreateResult;
import ro.axonsoft.internship172.model.batch.BatchGetResult;
import ro.axonsoft.internship172.model.batch.BatchSortCriterionType;
import ro.axonsoft.internship172.model.batch.ImtBatchCreate;
import ro.axonsoft.internship172.model.batch.ImtBatchCreateResult;
import ro.axonsoft.internship172.model.batch.ImtBatchGet;
import ro.axonsoft.internship172.model.batch.ImtBatchGetResult;
import ro.axonsoft.internship172.model.batch.ImtBatchSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerGet;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerSortCriterionType;

public class VehicleOwnerBusinessTests extends RecrutareBusinessTests {

	private static final String VHO_TN = "VEHICLE_OWNER";
	private static final String BATCH_TN = "BATCH";

	private static final String VHO_ID_CLM_NAME = "VEHICLE_OWNER_ID";
	private static final String BATCH_ID_CLM_NAME = "BATCH_ID";

	@Inject
	private VehicleOwnerBusiness vhoBusiness;

	@Inject
	private TestDbUtil dbUtil;

	@Test
	@DatabaseSetup(value = "VehicleOwnerBusinessTests-01-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "VehicleOwnerBusinessTests-01-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testCreateUserOnEmptyDatabase() throws Exception {
		dbUtil.setIdentity(VHO_TN, VHO_ID_CLM_NAME, 6);
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final VehicleOwnerCreateResult vhoCreateResult = vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
				.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
						.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
				.batch(ImtBatch.builder().batchId(0L).build()).build());
		assertThat(vhoCreateResult)
				.isEqualTo(ImtVehicleOwnerCreateResult.builder()
						.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
								.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
						.build());
		dbUtil.setIdentity(BATCH_TN, BATCH_ID_CLM_NAME, 6);
		final BatchCreateResult batchCreateResult = vhoBusiness
				.createBatch(ImtBatchCreate.builder().batch(ImtBatch.builder().build()).build());

		assertThat(batchCreateResult)
				.isEqualTo(ImtBatchCreateResult.builder().batch(ImtBatch.builder().build()).build());

		final BatchGetResult batchGetResult = vhoBusiness.getBatches(ImtBatchGet.builder()
				.addSort(ImtBatchSortCriterion.of(BatchSortCriterionType.BATCH_ID, SortDirection.DESC)).batchId(6L)
				.pagination(ImtPagination.of(1, 1)).build());
		assertThat(batchGetResult).isEqualTo(
				ImtBatchGetResult.builder().count(1).addList(ImtBatch.builder().batchId(6L).build()).pageCount(1)
						.pagination(ImtPagination.builder().page(1).pageSize(1).offset(0L).build()).build());

	}

	@Test
	@DatabaseSetup(value = "VehicleOwnerBusinessTests-02-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "VehicleOwnerBusinessTests-02-i.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testDeleteVehicleOwner() throws Exception {
		dbUtil.setIdentity(VHO_TN, VHO_ID_CLM_NAME, 6);
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final VehicleOwnerCreateResult vhoCreateResult = vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
				.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
						.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
				.batch(ImtBatch.builder().batchId(0L).build()).build());
		assertThat(vhoCreateResult)
				.isEqualTo(ImtVehicleOwnerCreateResult.builder()
						.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
								.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
						.build());
		final VehicleOwnerDeleteResult vhoDeleteResult = vhoBusiness.deleteVehicleOwner("KX636141");
		assertThat(vhoDeleteResult)
				.isEqualTo(ImtVehicleOwnerDeleteResult.builder()
						.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
								.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
						.build());
	}

	@Test
	@DatabaseSetup(value = "VehicleOwnerBusinessTests-02-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "VehicleOwnerBusinessTests-02-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testGetVehicleOwnersAfter2Inserts() throws Exception {
		dbUtil.setIdentity(VHO_TN, VHO_ID_CLM_NAME, 6);
		pushInstant(Instant.parse("2017-08-03T11:41:00.00Z"));
		final VehicleOwnerCreateResult vhoCreateResult = vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
				.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
						.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
				.batch(ImtBatch.builder().batchId(0L).build()).build());
		assertThat(vhoCreateResult)
				.isEqualTo(ImtVehicleOwnerCreateResult.builder()
						.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADC").roIdCard("KX636141")
								.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
						.build());

		final VehicleOwnerCreateResult vhoCreateResult0 = vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
				.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADD").roIdCard("KX636142")
						.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
				.batch(ImtBatch.builder().batchId(0L).build()).build());
		assertThat(vhoCreateResult0)
				.isEqualTo(ImtVehicleOwnerCreateResult.builder()
						.basic(ImtVehicleOwnerBasic.builder().regPlate("CJ84ADD").roIdCard("KX636142")
								.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).comentariu("000").build())
						.build());

		final VehicleOwnerGetResult vhoGetResult = vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet.builder()
				.addSort(ImtVehicleOwnerSortCriterion.builder().criterion(VehicleOwnerSortCriterionType.RO_ID_CARD)
						.direction(SortDirection.ASC).build())
				.roIdCard("KX636142").pagination(ImtPagination.of(1, 1)).build());
		assertThat(vhoGetResult).isEqualTo(ImtVehicleOwnerGetResult.builder().count(1).pageCount(1)
				.pagination(ImtPagination.of(1, 1))
				.addList(ImtVehicleOwnerBasicRecord.builder()
						.basic(ImtVehicleOwnerBasic.builder().comentariu("000").roIdCard("KX636142").regPlate("CJ84ADD")
								.issueDate(Instant.parse("2017-08-02T11:47:00.00Z")).build())
						.batch(ImtBatch.builder().batchId(0L).build()).build())
				.build());

	}
}
