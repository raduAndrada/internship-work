package ro.axonsoft.internship172.web.services;

import java.util.List;

import javax.inject.Inject;

import ro.axonsoft.internship172.data.domain.ResultError;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.domain.ResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.ErrorCriteria;
import ro.axonsoft.internship172.data.mappers.PageCriteria;
import ro.axonsoft.internship172.data.mappers.ResultUnregCarsCriteria;
import ro.axonsoft.internship172.data.services.ResultService;

public class ResultRestServiceImpl implements ResultRestService {


    ResultService resultService;

    @Inject
    public void setResultService(final ResultService resultService) {
        this.resultService = resultService;
    }

    @Override
    public int insertResultMetrics(final ResultMetrics metrics) {

        return resultService.insertResultMetrics(metrics);
    }

    @Override
    public ResultMetrics selectResultMetricsById(final Long id) throws InvalidDatabaseAccessException {

        return resultService.selectResultMetricsById(id);
    }

    @Override
    public List<ResultMetrics> findAllResults() {

        return resultService.findAllResults();
    }

    @Override
    public void insertResultError(final ResultError resultError) {
        resultService.insertResultError(resultError);
    }

    @Override
    public void insertResultUnregCarsCountByJud(final ResultUnregCarsCountByJud resultUnregCarsCountByJud) {
        resultService.insertResultUnregCarsCountByJud(resultUnregCarsCountByJud);

    }

    @Override
    public void insertResultMetricsWithErrors(final ResultMetrics resultMetrics, final ErrorCriteria errorCriteria,
            final ResultUnregCarsCriteria unregCriteria) throws DatabaseIntegrityViolationException {
        resultService.insertResultMetricsWithErrors(resultMetrics, errorCriteria, unregCriteria);

    }

    @Override
    public void updateResultByResultMetricsId(final ResultMetrics resultMetrics) {
        resultService.updateResultByResultMetricsId(resultMetrics);
    }

    @Override
    public void deleteResult(final Long id) {
        resultService.deleteResult(id);

    }

    @Override
    public List<ResultMetrics> getResultMetricsByBatchId(final Long id) {
        return resultService.getResultMetricsByBatchId(id);
    }

    @Override
    public Integer countResultMetricsByBatchId(final Long id) {
        return resultService.countResultMetricsByBatchId(id);
    }

    @Override
    public List<ResultMetrics> getResultMetricsPage(final PageCriteria pageCriteria) {
        return resultService.getResultMetricsPage(pageCriteria);
    }

}
