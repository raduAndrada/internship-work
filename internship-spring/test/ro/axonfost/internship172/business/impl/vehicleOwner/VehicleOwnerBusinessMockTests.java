package ro.axonfost.internship172.business.impl.vehicleOwner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import javax.inject.Inject;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.business.api.vehicleOwner.VehicleOwnerBusiness;
import ro.axonsoft.internship172.data.api.vehicleOwner.MdfVehicleOwnerEntity;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerDao;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntity;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntityGet;
import ro.axonsoft.internship172.data.tests.RecrutareBusinessMockTests;
import ro.axonsoft.internship172.model.base.ImtBatch;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorProperties;
import ro.axonsoft.internship172.model.error.ImtErrorProperties;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.MdfVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.MdfVehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreateResult;

public class VehicleOwnerBusinessMockTests extends RecrutareBusinessMockTests {

	@Inject
	private VehicleOwnerBusiness vhoBusiness;

	@MockBean
	private VehicleOwnerDao vhoDao;

	@Test
	public void testCreateVehicleOwner_success() throws Exception {

		final VehicleOwnerEntity vhoEntity = MdfVehicleOwnerEntity.create()
				.setRecord(MdfVehicleOwnerBasicRecord.create()
						.setBasic(MdfVehicleOwnerBasic.create().setComentariu("abc")
								.setIssueDate(Instant.parse("2017-08-03T11:41:00.00Z")).setRegPlate("HD10HXU")
								.setRoIdCard("HD123456"))
						.setBatch(ImtBatch.builder().batchId(0L).build()));
		given(vhoDao.addVehicleOwner(vhoEntity)).willReturn(1);
		final VehicleOwnerCreateResult vhoCreateResult = vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
				.basic(ImtVehicleOwnerBasic.builder().comentariu("ABC").regPlate("HD10HXU").roIdCard("HD123456")
						.issueDate(Instant.parse("2017-08-03T11:41:00.00Z")).build())
				.batch(ImtBatch.builder().batchId(0L).build()).build());
		assertThat(vhoCreateResult)
				.isEqualTo(ImtVehicleOwnerCreateResult
						.builder().basic(ImtVehicleOwnerBasic.builder().comentariu("abc").regPlate("HD10HXU")
								.roIdCard("HD123456").issueDate(Instant.parse("2017-08-03T11:41:00.00Z")).build())
						.build());
		verify(vhoDao).addVehicleOwner(vhoEntity);
	}

	public void testCreateVehicleOwner_fail_roIdCardExists() throws Exception {
		given(vhoDao.getVehicleOwner(isA(VehicleOwnerEntityGet.class)))
				.willReturn(ImmutableList.of(MdfVehicleOwnerEntity.create()));
		ErrorProperties errorProperties;
		try {
			vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
					.basic(ImtVehicleOwnerBasic.builder().comentariu("ABC").regPlate("HD10HXU").roIdCard("HD123456")
							.issueDate(Instant.parse("2017-08-03T11:41:00.00Z")).build())
					.batch(ImtBatch.builder().batchId(0L).build()).build());
			errorProperties = null;
		} catch (final BusinessException e) {
			errorProperties = e.getProperties();
		}
		// nu se face insert
		verify(vhoDao, never()).addVehicleOwner(isA(VehicleOwnerEntity.class));
		assertThat(errorProperties).isEqualTo(ImtErrorProperties.builder().build());
	}

}