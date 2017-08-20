package ro.axonsoft.internship172.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonsoft.internship172.data.domain.MdfConcreteResultMetrics;
import ro.axonsoft.internship172.data.domain.MdfResultError;
import ro.axonsoft.internship172.data.domain.MdfResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.result.ImtResultGet;
import ro.axonsoft.internship172.model.result.ImtResultSortCriterion;
import ro.axonsoft.internship172.model.result.ResultBasic;
import ro.axonsoft.internship172.model.result.ResultMetricsDeleteResult;
import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultRecord;
import ro.axonsoft.internship172.model.result.ResultSortCriterionType;

/**
 * Clasa controller pentru serviciul de rezultate
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "/rest/v1/results")
public class ResultRestController {

	ResultBusiness resultBusiness;

	@Inject
	public void setResultBusiness(ResultBusiness resultBusiness) {
		this.resultBusiness = resultBusiness;
	}

	/**
	 * Selectare rezultat dupa id
	 *
	 * @param id
	 *            identificatorul unic al rezultatului cautat
	 * @return rezultatul cu toate datele
	 */
	@RequestMapping(value = "/getResultById/{id}", method = RequestMethod.GET)
	public @ResponseBody ResultBasic getResultById(@PathVariable("id") final Long id)
			throws InvalidDatabaseAccessException {
		return resultBusiness.getResults(ImtResultGet.builder().resultMetricsId(id).build()).getList().get(0)
				.getBasic();
	}

	@RequestMapping(value = "/deleteResultById/{id}", method = RequestMethod.POST)
	public ResultMetricsDeleteResult deleteResultById(@PathVariable("id") final Long id)
			throws DatabaseIntegrityViolationException {
		final ResultMetricsDeleteResult resDeleted = resultBusiness.deleteResult(id);
		return resDeleted;
	}

	/**
	 * Selectarea unei paginii de rezultate
	 *
	 * @param batchId
	 *            identificatorul dupa care se cauta pagina
	 * @param pageSize
	 *            dimensiunea unei pagini
	 * @param currentPage
	 *            pagina curenta default-0
	 * @return o pagina de rezultate
	 * @throws InvalidDatabaseAccessException
	 *             daca id-ul de batch e invalid
	 */

	@RequestMapping(value = "/getResultsByPage/{batchId}/{pageSize}/{currentPage}", method = RequestMethod.GET)
	public @ResponseBody MdfConcreteResultMetrics[] getAllResultsPage(@PathVariable("batchId") final Long batchId,
			@PathVariable("pageSize") final int pageSize, @PathVariable("currentPage") final int currentPage

	) throws InvalidDatabaseAccessException {
		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}

		ResultMetricsGetResult result = null;
		final Integer resultsNumber = resultBusiness.getResults(ImtResultGet.builder().batchId(batchId).build())
				.getCount();
		final int startIndex = currentPage * pageSize;
		if (startIndex < resultsNumber) {
			result = resultBusiness.getResults(ImtResultGet.builder().batchId(batchId).batchId(batchId)
					.pagination(ImtPagination.builder().offset(new Long(startIndex)).pageSize(pageSize).build())
					.build());
		}
		System.out.println(result);
		final List<MdfConcreteResultMetrics> processResultList = Lists.newArrayList();
		for (final ResultRecord processResult : result.getList()) {
			final List<MdfResultError> resultErrors = Lists.newArrayList();
			if (processResult.getErrors() != null && processResult.getErrors().size() != 0) {
				processResult.getErrors().forEach(e -> resultErrors.add(MdfResultError.create()
						.setType(e.getBasic().getType()).setVehicleOwnerId(e.getBasic().getVehicleOwnerId())));
			}
			final List<MdfResultUnregCarsCountByJud> resultUnregCarsCountByJud = Lists.newArrayList();
			if (processResult.getUnregCars() != null && processResult.getUnregCars().size() != 0) {
				processResult.getUnregCars()
						.forEach(e -> resultUnregCarsCountByJud.add(MdfResultUnregCarsCountByJud.create()
								.setUnregCarsCount(e.getBasic().getUnregCarsCount())
								.setJudet(e.getBasic().getJudet())));
			}

			final MdfConcreteResultMetrics finalResult = MdfConcreteResultMetrics.create();

			finalResult.setOddToEvenRatio(processResult.getBasic().getOddToEvenRatio());
			finalResult.setPassedRegChangeDueDate(processResult.getBasic().getPassedRegChangeDueDate());
			finalResult.setBatchId(processResult.getBatch().getBatchId());
			finalResult.setResultErrors(resultErrors);
			finalResult.setUnregCarsCountByJud(resultUnregCarsCountByJud);
			finalResult.setResultProcessTime(processResult.getBasic().getResultProcessTime());
			processResultList.add(finalResult);
		}

		final MdfConcreteResultMetrics[] resultArray = processResultList
				.toArray(new MdfConcreteResultMetrics[processResultList.size()]);

		return resultArray;
	}

	/**
	 * Metoda de calculare al numarului de pagini disponibile pentru un batch in
	 * functie de dimensiunea unei pagini
	 *
	 * @param batchId
	 *            identificator dupa care se cauta numarul de pagini disponibile
	 * @param pageSize
	 *            dimensiunea unei pagini
	 * @return numele template-ului de afisare
	 * @throws InvalidDatabaseAccessException
	 *             daca batch-ul este invalid
	 */
	@RequestMapping(value = "/getNumberOfPages/{batchId}/{pageSize}", method = RequestMethod.GET)
	public @ResponseBody Integer getNumberOfPages(@PathVariable("batchId") final Long batchId,
			@PathVariable("pageSize") final int pageSize) throws InvalidDatabaseAccessException {
		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}
		final Integer resultsCount = resultBusiness.getResults(ImtResultGet.builder().batchId(batchId)
				.addSort(ImtResultSortCriterion.of(ResultSortCriterionType.BATCH_ID, SortDirection.ASC)).build())
				.getCount();
		Integer numOfPages = resultsCount / pageSize;
		if (resultsCount % pageSize != 0) {
			numOfPages++;
		}
		return numOfPages;
	}

	@ExceptionHandler({ InvalidDatabaseAccessException.class, DatabaseIntegrityViolationException.class })
	public String vehicleOwnerExceptionsHandling(final Exception exception) throws Exception {
		throw exception;
	}

}
