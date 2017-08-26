package ro.axonsoft.internship172.web.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultRecord;
import ro.axonsoft.internship172.web.model.ResultListForm;
import ro.axonsoft.internship172.web.rest.ProcessRestController;
import ro.axonsoft.internship172.web.rest.util.RestUrlResolver;

/**
 * Controller pentru procesare sau obtinere rezutlate
 *
 * @author Andra
 *
 */
@Controller
@RequestMapping(ResultController.URL_BASE)
public class ResultController {
	public static final String URL_BASE = "/results";
	private static final String URL_RESULT = "/result/";
	private static final String URL_PROCESS = "/process/";
	private static final String URL_FILE_UPLOAD = "/file-upload";

	private static final String BATCH_ID = "batchId";
	private static final String DB_PROCESS_PATH = "processing/db/";

	private static final String RESULT_METRICS_ID = "resultMetricsId";
	private static final String RESULT_METRICS_ID_PATH = "/{resultMetricsId}";
	private static final String BATCH_ID_PATH = "/{batchId}";
	private static final String DELETE_PATH = "/delete/" + RESULT_METRICS_ID;
	private static final String PAGE_SIZE = "pageSize";
	private static final String PAGE = "page";
	private static final String SEARCH = "search";
	private static final String RESULT_LIST = "resultList";
	private static final String PAGE_COUNT = "pageCount";
	private static final String RESULT_DETAILS_VIEW = "result-details";
	private static final String FILE_UPLOAD_DETAILS_VIEW = "file-upload-details";
	private static final String RESULT_LIST_VIEW = "result-list";
	private static final String REDIRECT_LIST = "redirect:" + URL_BASE + URL_RESULT;

	private static final ObjectError GENERIC_ERROR = new ObjectError(RESULT_LIST, new String[] { "generic.tech-error" },
			null, "A technical error has occured");

	private static final String RESULT_URI = "results";
	public static final String PROCESS_CSV_JSON_URI = "processing/csv/json";

	@Inject
	private RestTemplate restTemplate;
	@Inject
	private RestUrlResolver restUrlResolver;

	@ModelAttribute("resultListForm")
	public ResultListForm getResultListForm() {
		return new ResultListForm();
	}

	private static final Logger LOG = LoggerFactory.getLogger(ProcessRestController.class);

	/**
	 * Metoda de procesare pentru inregistrarile din baza de date
	 *
	 * @param model
	 *            partea grafica pe care se afiseaza rezultatul
	 * @param batchId
	 *            identificatorul pentru care se realizeaza procesarea
	 * @return numele template-ului pe care se afiseaza rezulatele
	 */
	@RequestMapping(value = URL_PROCESS, method = RequestMethod.GET)
	public String processSelectedBatch(@RequestParam("batchId") final Long batchId, final ModelMap modelMap) {

		final ResponseEntity<ResultRecord> resultRecord = restTemplate.postForEntity(
				restUrlResolver.resolveRestUri(DB_PROCESS_PATH, batchId.toString()), null, ResultRecord.class);
		modelMap.addAttribute("processResult", resultRecord.getBody());

		return RESULT_DETAILS_VIEW;
	}

	/**
	 * Maparea listei de rezultate pentru o acelasi batch
	 *
	 * @param model
	 *            partea grafica pe care se afiseaza rezultatele
	 * @param batchId
	 *            identificatorul pentru care se cauta istoricul
	 * @param pageSize
	 *            dimensiunea paginii pe care se afiseaza rezultatele
	 * @param currentPage
	 *            pagina curenta
	 * @return numele template-ului pe care se afiseaza lista
	 */
	@RequestMapping(value = BATCH_ID_PATH, method = RequestMethod.GET)
	public String listResults(@ModelAttribute final ResultListForm resultListForm, final BindingResult bindingResult,
			final ModelMap modelMap, @PathVariable(BATCH_ID) Long batchId) {
		ResponseEntity<ResultMetricsGetResult> resultGetResult = null;
		try {
			final UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromUri(restUrlResolver.resolveRestUri(RESULT_URI)).queryParam(BATCH_ID, batchId)
					.queryParam(PAGE_SIZE, Optional.ofNullable(resultListForm.getPageSize()).orElse(10))
					.queryParam(PAGE, Optional.ofNullable(resultListForm.getPage()).orElse(1))
					.queryParam(SEARCH, resultListForm.getSearch());
			LOG.info(uriBuilder.toString());
			resultGetResult = restTemplate.getForEntity(uriBuilder.build().toUri(), ResultMetricsGetResult.class);
		} catch (final Exception e) {
			LOG.error("Failed to fetch result list", e);
			bindingResult.addError(GENERIC_ERROR);
		}

		if (resultGetResult != null && resultGetResult.getStatusCode() == HttpStatus.OK) {
			modelMap.put(RESULT_LIST, resultGetResult.getBody().getList());
			modelMap.put(PAGE_COUNT, resultGetResult.getBody().getPageCount());
			modelMap.put(PAGE_SIZE, resultGetResult.getBody().getPagination().getPageSize());
		} else {
			modelMap.put(RESULT_LIST, ImmutableList.of());
			modelMap.put(PAGE_COUNT, 0);
			modelMap.put(PAGE_SIZE, 10);
		}
		return RESULT_LIST_VIEW;
	}

	/**
	 * Template pentru incarcarea unui fisier
	 *
	 * @param model
	 *            modelul
	 * @return template-ul
	 */
	@GetMapping(URL_FILE_UPLOAD)
	public String listUploadedFiles(final Model model) {
		return FILE_UPLOAD_DETAILS_VIEW;
	}

	/**
	 * Metoda de post pentru procesarea unui fisier
	 *
	 * @param file
	 *            fisier-ul incarcat de utilizator
	 * @param model
	 *            template-ul pe care va fi adaugat rezultatul
	 * @return
	 */
	@PostMapping(URL_FILE_UPLOAD)
	public String handleFileUpload(@RequestParam("file") final MultipartFile file, final Model model) {

		try {
			// metoda de transmitere a unui inputStream prin restTemplate
			final InputStream fis = file.getInputStream();
			final RequestCallback requestCallback = request -> {
				request.getHeaders().add("Content-type", "application/octet-stream");
				IOUtils.copy(fis, request.getBody());
			};

			final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setBufferRequestBody(false);
			restTemplate.setRequestFactory(requestFactory);
			final HttpMessageConverterExtractor<ResultRecord> responseExtractor = new HttpMessageConverterExtractor<>(
					ResultRecord.class, restTemplate.getMessageConverters());
			final ResultRecord tmp = restTemplate.execute(restUrlResolver.resolveRestUri(PROCESS_CSV_JSON_URI),
					HttpMethod.POST, requestCallback, responseExtractor);
			model.addAttribute("processResult", tmp);
			return RESULT_DETAILS_VIEW;

		} catch (IllegalStateException | IOException e) {

		}
		return RESULT_DETAILS_VIEW;

	}

	/**
	 * Metoda pentru definirea unui template custom in cazul aparitiei unei erori
	 *
	 * @param model
	 * @param exception
	 *            orice exceptie ar putea aparea
	 * @return template-ul
	 */
	@ExceptionHandler({ Exception.class })
	public String vehicleOwnerExceptionsHandling(final Model model, final Exception exception) {
		String msg = " ";
		if (exception.getMessage() != null) {
			msg += exception.getMessage();
		}
		model.addAttribute(exception.getClass().toString() + msg);
		return "exception";
	}

}
