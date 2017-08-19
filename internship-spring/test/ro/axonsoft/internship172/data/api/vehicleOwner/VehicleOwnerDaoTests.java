package ro.axonsoft.internship172.data.api.vehicleOwner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.TimeZone;

import javax.inject.Inject;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.data.api.batch.ImtBatchEntityGet;
import ro.axonsoft.internship172.data.tests.RecrutareDataTests;
import ro.axonsoft.internship172.data.tests.TestDbUtil;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.MdfResultBatch;
import ro.axonsoft.internship172.model.base.ResultBatch;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.batch.BatchSortCriterionType;
import ro.axonsoft.internship172.model.batch.ImtBatchSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.MdfVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.MdfVehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerSortCriterionType;

public class VehicleOwnerDaoTests extends RecrutareDataTests {

	private static final VehicleOwnerEntity VHO1 = MdfVehicleOwnerEntity.create().setVehicleOwnerId(0L)
			.setRecord(MdfVehicleOwnerBasicRecord.create()
					.setBasic(MdfVehicleOwnerBasic.create().setRoIdCard("KX636141")
							.setIssueDate(Instant.parse("2017-08-02T11:47:00.00Z")).setRegPlate("CJ84ADC")
							.setComentariu("000"))
					.setBatch(MdfResultBatch.create().setBatchId(0L)));

	private static final VehicleOwnerEntity VHO2 = MdfVehicleOwnerEntity.create().setVehicleOwnerId(1L)
			.setRecord(MdfVehicleOwnerBasicRecord.create()
					.setBasic(MdfVehicleOwnerBasic.create().setRoIdCard("KX636142").setComentariu("000")
							.setIssueDate(Instant.parse("2017-08-02T11:47:00.00Z")).setRegPlate("CJ84ADD"))
					.setBatch(MdfResultBatch.create().setBatchId(0L)));

	private static final ResultBatch BATCH1 = MdfResultBatch.create().setBatchId(6L);
	private static final ResultBatch BATCH2 = MdfResultBatch.create().setBatchId(7L);
	private static final ResultBatch BATCH3 = MdfResultBatch.create().setBatchId(8L);

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	@Inject
	private VehicleOwnerDao vehicleOwnerDao;

	@Inject
	private TestDbUtil dbUtil;

	@Test
	@DatabaseSetup(value = "VehicleOwnerDaoTests-01-i.xml", type = DatabaseOperation.TRUNCATE_TABLE)
	@ExpectedDatabase(value = "VehicleOwnerDaoTests-01-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testAddVehicleOwner_2consecutiveOnEmptyDatabase() throws Exception {
		dbUtil.setIdentity("VEHICLE_OWNER", "VEHICLE_OWNER_ID", 6);
		vehicleOwnerDao.addVehicleOwner(MdfVehicleOwnerEntity.create().from(VHO1).setVehicleOwnerId(null));
		vehicleOwnerDao.addVehicleOwner(MdfVehicleOwnerEntity.create().from(VHO2).setVehicleOwnerId(null));
		dbUtil.setIdentity("BATCH", "BATCH_ID", 9);
		vehicleOwnerDao.addBatch(MdfResultBatch.create().from(BATCH1));

	}

	@Test
	@DatabaseSetup("VehicleOwnerDaoTests-03-i.xml")
	public void testGetbBatch_difPag_orderCrtTmsAsc() {
		assertThat(vehicleOwnerDao.getBatch(ImtBatchEntityGet.builder()

				.sort(ImmutableList.of(ImtBatchSortCriterion.builder().criterion(BatchSortCriterionType.BATCH_ID)
						.direction(SortDirection.ASC).build()))
				.pagination(ImtPagination.of(1, 3)).build())).isEqualTo(ImmutableList.of(BATCH1, BATCH2, BATCH3));

	}

	@Test
	@DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	public void testGetVehicleOwner_difPag_orderCrtTmsAsc() {
		assertThat(vehicleOwnerDao.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()

				.sort(ImmutableList.of(ImtVehicleOwnerSortCriterion.builder()
						.criterion(VehicleOwnerSortCriterionType.RO_ID_CARD).direction(SortDirection.ASC).build()))
				.pagination(ImtPagination.of(1, 3)).build())).isEqualTo(ImmutableList.of(VHO1, VHO2));

	}

	@Test
	@DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	public void testGetVehicleOwner_ByRoIdCard_orderCrtTmsAsc() {
		assertThat(vehicleOwnerDao.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().idBatchSelect(ImmutableList.of(0L)).build())
				.sort(ImmutableList.of(ImtVehicleOwnerSortCriterion.builder()
						.criterion(VehicleOwnerSortCriterionType.RO_ID_CARD).direction(SortDirection.ASC).build()))
				.pagination(ImtPagination.of(1, 1)).search("KX636142").build())).isEqualTo(ImmutableList.of(VHO2));
	}

	@Test
	@DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	@ExpectedDatabase(value = "VehicleOwnerDaoTests-02-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testDeleteVehicleOwner_oneDelete() {

		vehicleOwnerDao.deleteVehicleOwner(ImtVehicleOwnerEntityDelete.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().addRoIdCardIncl("KX636142").build()).build());

	}

	@Test
	@DatabaseSetup("VehicleOwnerDaoTests-02-i.xml")
	@ExpectedDatabase(value = "VehicleOwnerDaoTests-03-e.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void testUpdateVehicleOwner_oneUpdate() {

		vehicleOwnerDao.updateVehicleOwner(ImtVehicleOwnerEntityUpdate.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().addIdIncl(1L).build()).comentariu("asta")
				.regPlate("abc1209s").build());

	}

}