package ro.axonsoft.internship172.web.controllers;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.ConcreteResultMetrics;
import ro.axonsoft.internship172.data.domain.MdfConcreteResultMetrics;
import ro.axonsoft.internship172.data.domain.MdfResultError;
import ro.axonsoft.internship172.data.domain.MdfResultMetrics;
import ro.axonsoft.internship172.data.domain.MdfResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.model.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.StreamVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessResult;
import ro.axonsoft.internship172.web.services.ResultRestService;

/**
 * Controller pentru bean-urile de procesare
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "/rest/v1/processing")
public class ProcessRestController {

	StreamVehicleOwnersProcessor streamProcessor;
	DbVehicleOwnersProcessor dbProcessor;
	ResultRestService resultService;

	private static final Logger LOG = LoggerFactory.getLogger(ProcessRestController.class);

	@Inject
	public void setDbProcessor(final DbVehicleOwnersProcessor dbProcessor) {
		this.dbProcessor = dbProcessor;
	}

	@Inject
	public void setResultRestService(final ResultRestService resultService) {
		this.resultService = resultService;
	}

	@Inject
	public void setStreamProcessor(final StreamVehicleOwnersProcessor streamProcessor) {
		this.streamProcessor = streamProcessor;
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

	@RequestMapping(value = "/csv/java-serial", method = RequestMethod.POST)
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

	@RequestMapping(value = "/csv/json", method = RequestMethod.POST)
	public ResponseEntity<MdfConcreteResultMetrics> csvProcess(final InputStream in) throws Exception {
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		streamProcessor.process(in, bout);
		final ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
		final VehicleOwnersProcessResult resultTmp = (VehicleOwnersProcessResult) objIn.readObject();
		final MdfConcreteResultMetrics res = MdfConcreteResultMetrics.create();
		res.setBatchId(0L);
		res.setOddToEvenRatio(resultTmp.getMetrics().getOddToEvenRatio());
		res.setPassedRegChangeDueDate(resultTmp.getMetrics().getPassedRegChangeDueDate());
		final List<MdfResultError> errors = Lists.newArrayList();
		final List<MdfResultUnregCarsCountByJud> unregCars = Lists.newArrayList();
		resultTmp.getErrors().forEach(e -> {
			final Integer typeTmp = e.getType();
			final MdfResultError error = MdfResultError.create();
			error.setType(typeTmp);
			error.setResultMetricsId(Long.valueOf(e.getLine()));
			errors.add(error);
		});
		for (final Map.Entry<String, Integer> entry : resultTmp.getMetrics().getUnregCarsCountByJud().entrySet()) {
			final MdfResultUnregCarsCountByJud unregCar = MdfResultUnregCarsCountByJud
					.create(Judet.valueOf(entry.getKey()), entry.getValue(), 0L, 0L);
			unregCars.add(unregCar);
		}
		res.setResultErrors(errors);
		res.setResultProcessTime(new java.sql.Timestamp(System.currentTimeMillis()));
		res.setUnregCarsCountByJud(unregCars);
		res.setResultMetricsId(0L);

		return ResponseEntity.ok(res);
	}

	/**
	 * Metoda de procesare folosind batch id
	 *
	 * @param batchId
	 *            identificatorul din tabela de batch
	 * @return ....
	 */

	@RequestMapping(value = "/db/{batchId}", method = RequestMethod.GET)
	public @ResponseBody ConcreteResultMetrics dbProcess(@PathVariable("batchId") final Long batchId) {
		LOG.info("procesarea batch-ului" + batchId);
		dbProcessor.process(batchId);
		final List<ResultMetrics> res = resultService.getResultMetricsByBatchId(batchId);

		final ResultMetrics processResultTemp = resultService
				.selectResultMetricsById(res.get(res.size() - 1).getResultMetricsId());
		final MdfResultMetrics processResult = MdfResultMetrics.create().from(processResultTemp);

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

		return finalResult;
	}

	@ExceptionHandler({ DatabaseIntegrityViolationException.class })
	public String vehicleOwnerExceptionsHandling(final Exception exception) throws Exception {
		throw exception;
	}

}
