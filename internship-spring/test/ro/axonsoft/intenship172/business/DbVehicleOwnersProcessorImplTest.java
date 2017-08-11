package ro.axonsoft.intenship172.business;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
import com.google.common.collect.Lists;

import ro.axonsoft.internship172.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.api.Judet;
import ro.axonsoft.internship172.data.domain.ImtResultError;
import ro.axonsoft.internship172.data.domain.ImtResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.domain.ResultError;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.domain.ResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.services.ResultService;
import ro.axonsoft.internship172.spring.SpringLevelApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringLevelApplication.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class)
@ActiveProfiles("test")
public class DbVehicleOwnersProcessorImplTest {

    private static final String DFT_PAGE_SIZE = "3";
    private static final String PAGE_SIZE_SYSTEM_PROP = "ro.axonsoft.internship.process.pageSize";

    @Inject
    private DbVehicleOwnersProcessor processor;
    @Inject
    private ResultService service;

    @Test
    @DatabaseSetup("DbVehicleOwnersProcessorImplTest-i01.xml")
    public void processComplexTest() {
        System.setProperty(PAGE_SIZE_SYSTEM_PROP, DFT_PAGE_SIZE);

        processor.process(100L);
        final List<ResultMetrics> theResultMetricsList = service.findAllResults();

        final ResultMetrics result = theResultMetricsList.get(theResultMetricsList.size()-1);
        assertThat(result).isNotNull();
        assertThat(result.getOddToEvenRatio()).isEqualTo(66);
        assertThat(result.getPassedRegChangeDueDate()).isEqualTo(2);
        final Long resultMetricsId = result.getResultMetricsId();
        final ArrayList<ResultError> errors = Lists.newArrayList();
        assertThat(result.getUnregCarsCountByJud())
                .isNotNull()
                .isNotEmpty();
        final ArrayList<Long> errIds = Lists.newArrayList();

        result.getResultErrors().forEach(e -> errors
                .add(ImtResultError.of(e.getType(), e.getResultErrorId(), e.getVehicleOwnerId(), e.getResultMetricsId())));

        result.getResultErrors().forEach(e -> errIds.add(e.getResultErrorId()));
        int i = 0;
        assertThat(ImmutableList.of(errors))
                .isEqualTo(ImmutableList.of(Lists
                        .newArrayList(
                                ImtResultError.builder()
                                        .type(1)
                                        .vehicleOwnerId(7L)
                                        .resultErrorId(errIds.get(i++))
                                        .resultMetricsId(resultMetricsId)
                                        .build(),
                                ImtResultError.builder()
                                        .type(2)
                                        .vehicleOwnerId(7L)
                                        .resultErrorId(errIds.get(i++))
                                        .resultMetricsId(resultMetricsId)
                                        .build(),
                                ImtResultError.builder()
                                        .type(1)
                                        .vehicleOwnerId(8L)
                                        .resultErrorId(errIds.get(i++))
                                        .resultMetricsId(resultMetricsId)
                                        .build(),
                                ImtResultError.builder()
                                        .type(0)
                                        .vehicleOwnerId(10L)
                                        .resultErrorId(errIds.get(i++))
                                        .resultMetricsId(resultMetricsId)
                                        .build())));
        final ArrayList<ResultUnregCarsCountByJud> unregCars = Lists.newArrayList();
        result.getUnregCarsCountByJud().forEach(
                            e -> unregCars.add(ImtResultUnregCarsCountByJud.builder()
                                    .judet(e.getJudet())
                                    .unregCarsCount(e.getUnregCarsCount())
                                    .resultMetricsId(e.getResultMetricsId())
                                    .unregCarsId(e.getUnregCarsId())
                                     .build()));

        assertThat(ImmutableList.of(unregCars))
                .isEqualTo(ImmutableList.of(Lists
                        .newArrayList(
                                ImtResultUnregCarsCountByJud.of(Judet.MM, 1,
                                        result.getUnregCarsCountByJud().get(0).getUnregCarsId(),
                                        resultMetricsId))));

    }


    @Test
    @DatabaseSetup("DbVehicleOwnersProcessorImplTest-i01.xml")
    public void processSimpleTest() {
        System.setProperty(PAGE_SIZE_SYSTEM_PROP, DFT_PAGE_SIZE);

        processor.process(3L);

        final List<ResultMetrics> theResultMetricsList = service.findAllResults();
        final ResultMetrics result = theResultMetricsList.get(theResultMetricsList.size() - 1);
        assertThat(result).isNotNull();
        assertThat(result.getResultErrors()).isNullOrEmpty();
        assertThat(result.getUnregCarsCountByJud()).isNullOrEmpty();
        assertThat(result.getBatchId()).isEqualTo(3L);
        assertThat(result.getOddToEvenRatio()).isEqualTo(100);
        assertThat(result.getPassedRegChangeDueDate()).isEqualTo(0);

    }
}
