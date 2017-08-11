package ro.axonsoft.internship172.data.impl;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.ImtResultError;
import ro.axonsoft.internship172.data.domain.ImtResultMetrics;
import ro.axonsoft.internship172.data.domain.ImtResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.domain.ResultError;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.domain.ResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.ErrorCriteria;
import ro.axonsoft.internship172.data.mappers.PageCriteria;
import ro.axonsoft.internship172.data.mappers.ResultDao;
import ro.axonsoft.internship172.data.mappers.ResultUnregCarsCriteria;
import ro.axonsoft.internship172.data.services.ResultService;

/**
 * Serviciu pentru procesarea tabelelor de rezultat
 * contine aceleasi metode ca si mapper-ul
 * layer pentru realiza legatura dintre baza de date si nivelul de business
 *
 * @author Andrada
 *
 */

public class ResultServiceImpl implements ResultService {

    private ResultDao resultDao;

    @Inject
    public void setResultMetricsMapper(final ResultDao resultDao) {
        this.resultDao = resultDao;
    }

    @Override
    public int insertResultMetrics(final ResultMetrics metrics) {
        return resultDao.insertResultMetrics(metrics);

    }

    @Override
    public ResultMetrics selectResultMetricsById(final Long id) throws InvalidDatabaseAccessException {
        final ResultMetrics temp = resultDao.selectResultMetricsById(id);
        if (temp == null) {
            throw new InvalidDatabaseAccessException("rezultatul solicitat nu exista");
        }
        final List<ResultError> errors = resultDao.selectResultErrorByMetricsId(temp.getResultMetricsId());
        final List<ResultUnregCarsCountByJud> unregCars = resultDao.selectResultUnregCarsCountByMetricsId(temp.getResultMetricsId());
        return ImtResultMetrics.builder()
                .batchId(temp.getBatchId())
                .oddToEvenRatio(temp.getOddToEvenRatio())
                .passedRegChangeDueDate(temp.getPassedRegChangeDueDate())
                .resultMetricsId(temp.getResultMetricsId())
                .resultErrors(errors)
                .unregCarsCountByJud(unregCars)
                .build();
    }

    @Override
    public List<ResultMetrics> findAllResults() {
        final List<ResultMetrics> temp = resultDao.selectAllResultMetrics();
        final List<ResultMetrics> metricsList = Lists.newArrayList();
        temp.forEach(e -> metricsList.add(selectResultMetricsById(e.getResultMetricsId())));
        return metricsList;
    }

    @Override
    public void insertResultError(final ResultError resultError) {
        resultDao.insertResultError(resultError);

    }

    @Override
    public void insertResultUnregCarsCountByJud(final ResultUnregCarsCountByJud resultUnregCarsCountByJud) {
        resultDao.insertResultUnregCarsCountByJud(resultUnregCarsCountByJud);

    }

    @Override
    public void insertResultMetricsWithErrors(final ResultMetrics resultMetrics, final ErrorCriteria errorCriteria,
            final ResultUnregCarsCriteria unregCriteria) throws DatabaseIntegrityViolationException {
        if (resultMetrics.getBatchId() != null && resultDao.selectBatchById(resultMetrics.getBatchId()) == null){
            throw new DatabaseIntegrityViolationException("batch-ul selectat nu exista");
        }

//        if (resultDao.getResultMetricsByBatchId(resultMetrics.getBatchId()).size() != 0) {
//
//            updateResultByResultMetricsId(resultMetrics);
//
//        } else {
            resultDao.insertResultMetrics(resultMetrics);
            if (errorCriteria != null) {
                final List<ResultError> errors = Lists.newArrayList();
                resultMetrics.getResultErrors().forEach(e -> errors.add(ImtResultError.builder()
                        .type(e.getType())
                        .resultErrorId(e.getResultErrorId())
                        .resultMetricsId(resultMetrics.getResultMetricsId())
                        .vehicleOwnerId(e.getVehicleOwnerId())
                        .build()));

                errors.forEach(e -> resultDao.insertResultError(e));
            }
            if (unregCriteria != null) {
                final List<ResultUnregCarsCountByJud> unregCars = Lists.newArrayList();
                resultMetrics.getUnregCarsCountByJud().forEach(e -> unregCars.add(
                        ImtResultUnregCarsCountByJud.builder()
                                .judet(e.getJudet())
                                .resultMetricsId(resultMetrics.getResultMetricsId())
                                .unregCarsCount(e.getUnregCarsCount())
                                .unregCarsId(e.getUnregCarsId())
                                .build()));

                unregCars.forEach(e -> resultDao.insertResultUnregCarsCountByJud(e));
            }


    }

    @Override
    public void updateResultByResultMetricsId(final ResultMetrics resultMetrics) {
        resultDao.updateResultMetricsById(resultMetrics);
        final List<ResultError> errors = Lists.newArrayList();
        if (resultMetrics.getResultErrors() != null) {
            resultMetrics.getResultErrors().forEach(e -> errors.add(ImtResultError.builder()
                    .type(e.getType())
                    .resultErrorId(e.getResultErrorId())
                    .resultMetricsId(resultMetrics.getResultMetricsId())
                    .vehicleOwnerId(e.getVehicleOwnerId())
                    .build()));

            errors.forEach(e -> resultDao.updateResulErrorsByMetricsId(e));
        }

        final List<ResultUnregCarsCountByJud> unregCars = Lists.newArrayList();
        if (resultMetrics.getUnregCarsCountByJud() != null) {
            resultMetrics.getUnregCarsCountByJud().forEach(e -> unregCars.add(
                    ImtResultUnregCarsCountByJud.builder()
                            .judet(e.getJudet())
                            .resultMetricsId(resultMetrics.getResultMetricsId())
                            .unregCarsCount(e.getUnregCarsCount())
                            .unregCarsId(e.getUnregCarsId())
                            .build()));

            unregCars.forEach(e -> resultDao.updateResultUnregCarsByMetricsId(e));
        }

    }

    @Override
    public void deleteResult(final Long id) {
        if (selectResultMetricsById(id) == null){
            throw new DatabaseIntegrityViolationException("rezulatul nu exista");
        }
        resultDao.deleteResultUnregCarsCountByMetricsId(id);
        resultDao.deleteResultErrosByMetricsId(id);
        resultDao.deleteResultMetricsById(id);

    }

	@Override
	public List<ResultMetrics> getResultMetricsByBatchId(final Long id) {
		return resultDao.getResultMetricsByBatchId(id);
	}

    @Override
    public Integer countResultMetricsByBatchId(final Long id) {

        return resultDao.countResultMetricsByBatchId(id);
    }

    @Override
    public List<ResultMetrics> getResultMetricsPage(final PageCriteria pageCriteria) {
         final List<ResultMetrics> resultTemp = resultDao.getResultMetricsPage(pageCriteria);
         final List<ResultUnregCarsCountByJud> resultUnreg = Lists.newArrayList();
         final List<ResultError> resultErrors = Lists.newArrayList();
         final List<ResultMetrics> resultFinal = Lists.newArrayList();
         resultTemp.forEach(e ->{
             final Long id = e.getResultMetricsId();
             resultUnreg.addAll(resultDao.selectResultUnregCarsCountByMetricsId(id));
             resultErrors.addAll(resultDao.selectResultErrorByMetricsId(id));
             resultFinal.add(ImtResultMetrics.builder()
                     .resultErrors(resultErrors)
                     .resultProcessTime(e.getResultProcessTime())
                     .passedRegChangeDueDate(e.getPassedRegChangeDueDate())
                     .batchId(e.getBatchId())
                     .oddToEvenRatio(e.getOddToEvenRatio())
                     .resultMetricsId(e.getResultMetricsId())
                     .build());
         });
     return resultFinal;
    }

}
