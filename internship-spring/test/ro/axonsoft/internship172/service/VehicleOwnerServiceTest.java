package ro.axonsoft.internship172.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;
import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.data.services.VehicleOwnerService;
import ro.axonsoft.internship172.model.base.Batch;
import ro.axonsoft.internship172.model.base.MdfBatch;
import ro.axonsoft.internship172.spring.SpringLevelApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringLevelApplication.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@ActiveProfiles("test")
public class VehicleOwnerServiceTest {

	@Inject
	private VehicleOwnerService service;

	@BeforeClass
	public static void setup() {

	}

	@Test
	@DatabaseSetup("VehicleOwnerServiceTest-i01.xml")
	public void selectVehicleOwnerByIdTest() {
		final VehicleOwner vehicleOwner = service.selectVehicleOwnerById(0L);
		assertThat(vehicleOwner).isNotNull();
		assertThat(vehicleOwner.getRoIdCard()).isEqualTo("KX636141");
	}

	@Test
	@DatabaseSetup("VehicleOwnerServiceTest-i01.xml")
	public void insertBatchTest() {
		final Batch theBatch = MdfBatch.create();
		service.insertBatch(theBatch);
		assertThat(theBatch).isNotNull();
		assertThat(theBatch.getBatchId()).isNotNull();
	}

	@Test
	@DatabaseSetup("VehicleOwnerServiceTest-i01.xml")
	public void selectLastBatchTest() {
		final Batch theBatch = MdfBatch.create(service.selectLastBatch());
		assertThat(theBatch).isNotNull();
		assertThat(theBatch.getBatchId()).isNotNull();
	}

	@Test
	@DatabaseSetup("VehicleOwnerServiceTest-i01.xml")
	public void selectAllBatchesTest() {
		final List<Batch> theBatches = Lists.newArrayList(service.selectAllBatches());
		assertThat(theBatches).isNotNull();

	}

}
