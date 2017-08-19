package ro.axonsoft.internship172.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

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
import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.data.domain.ImtResultMetrics;
import ro.axonsoft.internship172.data.domain.ImtResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.impl.ResultServiceImpl;
import ro.axonsoft.internship172.data.mappers.ImtResultUnregCarsCriteria;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.spring.SpringLevelApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringLevelApplication.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@ActiveProfiles("test")
public class ResultServiceImplTest {
	@Inject
	private ResultServiceImpl service;

	@Test
	@DatabaseSetup("ResultServiceTest-i01.xml")
	public void findAllResultWithErrorsTest() {
		final List<ResultMetrics> theResultMetricsList = service.findAllResults();
		assertThat(theResultMetricsList).isNotNull();
		System.out.println(theResultMetricsList.toString());
		assertThat(theResultMetricsList.size()).isEqualTo(2);
		assertThat(theResultMetricsList.get(0).getOddToEvenRatio()).isEqualTo(100);
		assertThat(theResultMetricsList.get(0).getResultErrors()).isNullOrEmpty();
		assertThat(theResultMetricsList.get(0).getUnregCarsCountByJud().size()).isEqualTo(1);
	}

	@Test
	@DatabaseSetup("ResultServiceTest-i01.xml")
	public void findAllResultsTest() {
		final List<ResultMetrics> theResultMetricsList = service.findAllResults();
		assertThat(theResultMetricsList).isNotNull();
		assertThat(theResultMetricsList.size()).isEqualTo(2);
		assertThat(theResultMetricsList.get(0).getOddToEvenRatio()).isEqualTo(100);
		theResultMetricsList.get(0).getResultErrors().forEach(e -> assertThat(e.getResultErrorId()).isNull());
		assertThat(theResultMetricsList.get(0).getUnregCarsCountByJud().size()).isEqualTo(1);
	}

	@Test
	@DatabaseSetup("ResultServiceTest-i01.xml")
	public void selectResultMetricsByIdTest() {
		final ResultMetrics theResultMetrics = service.selectResultMetricsById(0L);
		System.out.println(theResultMetrics.toString());
		assertThat(theResultMetrics).isNotNull();
		assertThat(theResultMetrics.getUnregCarsCountByJud()).isNotNull();
		assertThat(theResultMetrics.getOddToEvenRatio()).isEqualTo(100);
		assertThat(theResultMetrics.getResultErrors()).isNullOrEmpty();
	}



	@Test
	@DatabaseSetup("ResultServiceTest-i01.xml")
	public void updateResultMetricsWithoutErrorsTest() {
	    final List<ResultMetrics> beforeInsertionResultList = service.findAllResults();
		final ImtResultUnregCarsCountByJud unregCars =ImtResultUnregCarsCountByJud.builder()
					.judet(Judet.AB)
				    .unregCarsCount(3)
				    .build();

        final ResultMetrics theResultMetrics = ImtResultMetrics.builder()
				.oddToEvenRatio(55)
				.passedRegChangeDueDate(100)
				.batchId(1L)
				.addAllUnregCarsCountByJud(ImmutableList.of(unregCars))
				.build();

		service.insertResultMetricsWithErrors(theResultMetrics, null, ImtResultUnregCarsCriteria.of());


		final List<ResultMetrics> afterInsertionResultList = service.findAllResults();
		assertThat(beforeInsertionResultList.size()).isNotEqualTo(afterInsertionResultList.size());

	}


}
