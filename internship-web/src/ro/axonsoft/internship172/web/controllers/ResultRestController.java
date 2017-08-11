package ro.axonsoft.internship172.web.controllers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.MdfConcreteResultMetrics;
import ro.axonsoft.internship172.data.domain.MdfResultError;
import ro.axonsoft.internship172.data.domain.MdfResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.ImtPageCriteria;
import ro.axonsoft.internship172.web.services.ResultRestService;

/**
 * Clasa controller pentru serviciul de rezultate
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "/rest/v1/results")
public class ResultRestController {

	ResultRestService resultService;

	@Inject
	public void setResultRestService(final ResultRestService resultService) {
		this.resultService = resultService;
	}

	/**
	 * Selectare rezultat dupa id
	 *
	 * @param id
	 *            identificatorul unic al rezultatului cautat
	 * @return rezultatul cu toate datele
	 */
	@RequestMapping(value = "/getResultById/{id}", method = RequestMethod.GET)
	public @ResponseBody ResultMetrics getResultById(@PathVariable("id") final Long id)
			throws InvalidDatabaseAccessException {
		return resultService.selectResultMetricsById(id);
	}

	@RequestMapping(value = "/deleteResultById/{id}", method = RequestMethod.POST)
	public String deleteResultById(@PathVariable("id") final Long id) throws DatabaseIntegrityViolationException {
		resultService.deleteResult(id);
		return "Operatia de stergere s-a realizat cu succes";
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

		final Integer resultsNumber = resultService.countResultMetricsByBatchId(batchId);
		final int startIndex = currentPage * pageSize;
		List<ResultMetrics> resultsList = null;
		if (startIndex < resultsNumber) {
			resultsList = Lists.newArrayList(
					resultService.getResultMetricsPage(ImtPageCriteria.of(startIndex, pageSize, batchId)));
		}
		if (resultsList == null) {
			throw new InvalidDatabaseAccessException("pagina depaseste datele din baza de date");
		}
		final List<MdfConcreteResultMetrics> processResultList = Lists.newArrayList();
		for (final ResultMetrics processResult : resultsList) {
			final List<MdfResultError> resultErrors = Lists.newArrayList();
			if (processResult.getResultErrors() != null && processResult.getResultErrors().size() != 0) {
				processResult.getResultErrors().forEach(e -> resultErrors.add(MdfResultError.create().from(e)));
			}
			final List<MdfResultUnregCarsCountByJud> resultUnregCarsCountByJud = Lists.newArrayList();
			if (processResult.getUnregCarsCountByJud() != null && processResult.getUnregCarsCountByJud().size() != 0) {
				processResult.getUnregCarsCountByJud()
						.forEach(e -> resultUnregCarsCountByJud.add(MdfResultUnregCarsCountByJud.create().from(e)));
			}

			final MdfConcreteResultMetrics finalResult = MdfConcreteResultMetrics.create();

			finalResult.setOddToEvenRatio(processResult.getOddToEvenRatio());
			finalResult.setPassedRegChangeDueDate(processResult.getPassedRegChangeDueDate());
			finalResult.setResultMetricsId(processResult.getResultMetricsId());
			finalResult.setBatchId(processResult.getBatchId());
			finalResult.setResultErrors(resultErrors);
			finalResult.setUnregCarsCountByJud(resultUnregCarsCountByJud);
			finalResult.setResultProcessTime(processResult.getResultProcessTime());
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
		final Integer resultsCount = resultService.countResultMetricsByBatchId(batchId);
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
