package ro.axonsoft.internship172.rest.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.model.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.StreamVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessResult;
import ro.axonsoft.internship172.model.base.ImtBatch;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.result.ImtResultBasic;
import ro.axonsoft.internship172.model.result.ImtResultErrorBasic;
import ro.axonsoft.internship172.model.result.ImtResultErrorRecord;
import ro.axonsoft.internship172.model.result.ImtResultGet;
import ro.axonsoft.internship172.model.result.ImtResultRecord;
import ro.axonsoft.internship172.model.result.ImtResultUnregCarsCountByJudBasic;
import ro.axonsoft.internship172.model.result.ImtResultUnregCarsCountByJudRecord;
import ro.axonsoft.internship172.model.result.ResultErrorRecord;
import ro.axonsoft.internship172.model.result.ResultRecord;
import ro.axonsoft.internship172.model.result.ResultSortCriterionType;
import ro.axonsoft.internship172.model.result.ResultUnregCarsCountByJudRecord;

/**
 * Controller pentru bean-urile de procesare
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "/v1/processing")
public class ProcessRestController {

	public static final String URL_PROCESS_CSV_SERIAL = "/csv/java-serial";
	public static final String URL_PROCESS_CSV_JSON = "/csv/json";
	public static final String BATCH_PATH = "{batchId}";
	public static final String URL_DB_PROCESS = "/db/" + BATCH_PATH;

	StreamVehicleOwnersProcessor streamProcessor;
	DbVehicleOwnersProcessor dbProcessor;
	ResultBusiness resultBusiness;

	private static final Logger LOG = LoggerFactory.getLogger(ProcessRestController.class);

	@Inject
	public void setDbProcessor(final DbVehicleOwnersProcessor dbProcessor) {
		this.dbProcessor = dbProcessor;
	}

	@Inject
	public void setStreamProcessor(final StreamVehicleOwnersProcessor streamProcessor) {
		this.streamProcessor = streamProcessor;
	}

	@Inject
	public void setResultBusiness(ResultBusiness resultBusiness) {
		this.resultBusiness = resultBusiness;
	}

	/**
	 * Procesare folosind stream-uri
	 *
	 * @param stream
	 *            cu datele de intrare
	 * @param out
	 *            obiect serializat cu date de iesire
	 * @throws Exception
	 *             stream-ul este invalid
	 */

	@RequestMapping(value = URL_PROCESS_CSV_SERIAL, method = RequestMethod.POST)
	public void csvProcess(final InputStream in, final OutputStream out) throws Exception {
		streamProcessor.process(in, out);
	}

	/**
	 * Procesarea folosind fisiere
	 *
	 * @param in
	 *            fisier-ul de intrare
	 * @return obiect serializat
	 * @throws Exception
	 *             fisier-ul de intrare, invalid
	 */

	@RequestMapping(value = URL_PROCESS_CSV_JSON, method = RequestMethod.POST)
	public ResponseEntity<ResultRecord> csvProcess(final InputStream in) throws Exception {
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		streamProcessor.process(in, bout);
		final ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
		final VehicleOwnersProcessResult resultTmp = (VehicleOwnersProcessResult) objIn.readObject();

		final List<ResultErrorRecord> errors = Lists.newArrayList();
		final List<ResultUnregCarsCountByJudRecord> unregCars = Lists.newArrayList();
		resultTmp.getErrors().forEach(e -> {
			final Integer typeTmp = e.getType();
			final ResultErrorRecord error = ImtResultErrorRecord.builder().basic(
					ImtResultErrorBasic.builder().type(typeTmp).vehicleOwnerId(Long.valueOf(e.getLine())).build())
					.build();
			errors.add(error);
		});
		for (final Map.Entry<String, Integer> entry : resultTmp.getMetrics().getUnregCarsCountByJud().entrySet()) {
			final ResultUnregCarsCountByJudRecord unregCar = ImtResultUnregCarsCountByJudRecord.builder()
					.basic(ImtResultUnregCarsCountByJudBasic.builder().judet(Judet.valueOf(entry.getKey()))
							.unregCarsCount(entry.getValue()).build())
					.build();
			unregCars.add(unregCar);
		}

		final ResultRecord res = ImtResultRecord.builder().batch(ImtBatch.of(0L))
				.basic(ImtResultBasic.builder().oddToEvenRatio(resultTmp.getMetrics().getOddToEvenRatio())
						.passedRegChangeDueDate(resultTmp.getMetrics().getPassedRegChangeDueDate()).build())
				.addAllErrors(errors).addAllUnregCars(unregCars).build();

		return ResponseEntity.ok(res);
	}

	/**
	 * Metoda de procesare folosind batch id
	 *
	 * @param batchId
	 *            identificatorul din tabela de batch
	 * @return ....
	 */

	@RequestMapping(value = URL_DB_PROCESS, method = RequestMethod.POST)
	public ResponseEntity<ResultRecord> dbProcess(@PathVariable("batchId") final Long batchId) {
		dbProcessor.process(batchId);
		final ResultRecord res = resultBusiness.getResults(ImtResultGet.builder().batchId(batchId)
				.addSort(ResultSortCriterionType.RESULT_PROCESS_TIME, SortDirection.DESC).pagination(1, 1).build())
				.getList().get(0);
		return ResponseEntity.ok(res);

	}

	@ExceptionHandler({ DatabaseIntegrityViolationException.class })
	public String vehicleOwnerExceptionsHandling(final Exception exception) throws Exception {
		throw exception;
	}

}
