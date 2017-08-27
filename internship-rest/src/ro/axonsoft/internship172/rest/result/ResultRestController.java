package ro.axonsoft.internship172.rest.result;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.result.ImtResultGet;
import ro.axonsoft.internship172.model.result.ImtResultSortCriterion;
import ro.axonsoft.internship172.model.result.ResultBasic;
import ro.axonsoft.internship172.model.result.ResultMetricsDeleteResult;
import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultSortCriterionType;
import ro.axonsoft.internship172.rest.util.RestUtil;

/**
 * Clasa controller pentru serviciul de rezultate
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "v1/results")
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

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<ResultMetricsGetResult> getResults(@RequestParam final Long batchId,
			@RequestParam(defaultValue = "1") final Integer page,
			@RequestParam(defaultValue = "20") final Integer pageSize,
			@RequestParam(defaultValue = "RESULT_PROCESS_TIME:ASC") final List<String> sort,
			@RequestParam(value = "search", required = false) final String search) {

		final ResultMetricsGetResult resGetResult = resultBusiness.getResults(ImtResultGet.builder().batchId(batchId)
				.pagination(ImtPagination.of(page, pageSize)).sort(RestUtil.parseSort(sort,
						ResultSortCriterionType.class, (x, y) -> ImtResultSortCriterion.of(x, y)))
				.search(search).build());
		return ResponseEntity.ok(resGetResult);
	}

	public String vehicleOwnerExceptionsHandling(final Exception exception) throws Exception {
		throw exception;
	}

}
