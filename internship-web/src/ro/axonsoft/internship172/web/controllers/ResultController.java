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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorProperties.VarValue;
import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultRecord;
import ro.axonsoft.internship172.web.model.ImtSuccessMessage;
import ro.axonsoft.internship172.web.model.ResultListForm;
import ro.axonsoft.internship172.web.util.RestUrlResolver;

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
	private static final String RESULT_PATH = "/result/";
	private static final String PROCESS_PATH = "/process/";
	private static final String FILE_UPLOAD_PATH = "/file-upload";

	private static final String BATCH_ID = "batchId";
	private static final String DB_PROCESS_PATH = "processing/db/";

	private static final String RESULT_METRICS_ID = "resultMetricsId";
	private static final String RESULT_METRICS_ID_PATH = "/{resultMetricsId}";
	private static final String BATCH_ID_PATH = "/{batchId}";
	private static final String DELETE_PATH = "/delete" + RESULT_METRICS_ID_PATH;
	private static final String PAGE_SIZE = "pageSize";
	private static final String PAGE = "page";
	private static final String SEARCH = "search";
	private static final String START = "start";
	private static final String END = "end";
	private static final String START_INDEX = "startIndex";
	private static final String END_INDEX = "endIndex";
	private static final String DOTS_NEXT = "dotsNext";
	private static final String DOTS_PREV = "dotsPrev";

	private static final String RESULT_LIST = "resultList";
	private static final String PAGE_COUNT = "pageCount";
	private static final String RESULT_DETAILS_VIEW = "result-details";
	private static final String FILE_UPLOAD_DETAILS_VIEW = "file-upload-details";
	private static final String RESULT_LIST_VIEW = "result-list";
	private static final String REDIRECT_LIST = "redirect:" + URL_BASE + RESULT_PATH;

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

	private static final Logger LOG = LoggerFactory.getLogger(ResultController.class);

	/**
	 * Metoda de procesare pentru inregistrarile din baza de date
	 *
	 * @param model
	 *            partea grafica pe care se afiseaza rezultatul
	 * @param batchId
	 *            identificatorul pentru care se realizeaza procesarea
	 * @return numele template-ului pe care se afiseaza rezulatele
	 */
	@RequestMapping(value = PROCESS_PATH, method = RequestMethod.GET)
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
		final Integer currentPage = (resultListForm.getPage() != null) ? resultListForm.getPage() : 1;
		final Integer startIndex = (currentPage - 3 > 1) ? currentPage : 1;
		final boolean start = (startIndex > 3) ? true : false;
		final boolean dotsPrev = (startIndex > 4) ? true : false;
		final Integer endIndex = (currentPage + 3 < resultGetResult.getBody().getPageCount() ? currentPage + 3
				: resultGetResult.getBody().getPageCount());
		final boolean end = (currentPage < resultGetResult.getBody().getPageCount() - 3 ? true : false);
		final boolean dotsNext = (currentPage < resultGetResult.getBody().getPageCount() - 4 ? true : false);

		if (resultGetResult != null && resultGetResult.getStatusCode() == HttpStatus.OK) {
			modelMap.put(RESULT_LIST, resultGetResult.getBody().getList());
			modelMap.put(PAGE_COUNT, resultGetResult.getBody().getPageCount());
			modelMap.put(PAGE_SIZE, resultGetResult.getBody().getPagination().getPageSize());
			modelMap.put(START, start);
			modelMap.put(END, end);
			modelMap.put(START_INDEX, startIndex);
			modelMap.put(END_INDEX, endIndex);
			modelMap.put(DOTS_NEXT, dotsNext);
			modelMap.put(DOTS_PREV, dotsPrev);
		} else {
			modelMap.put(RESULT_LIST, ImmutableList.of());
			modelMap.put(PAGE_COUNT, 0);
			modelMap.put(PAGE_SIZE, 10);
			modelMap.put(START, start);
			modelMap.put(END, end);
			modelMap.put(START_INDEX, startIndex);
			modelMap.put(END_INDEX, endIndex);
			modelMap.put(DOTS_NEXT, dotsNext);
			modelMap.put(DOTS_PREV, dotsPrev);
		}
		return RESULT_LIST_VIEW;
	}

	@RequestMapping(path = DELETE_PATH, method = RequestMethod.POST)
	public String postDelete(@PathVariable final Long resultMetricsId, final ModelMap modelMap,
			final RedirectAttributes redirectAttributes) {
		final ResultListForm resultListForm = new ResultListForm();
		final Errors errors = new BeanPropertyBindingResult(resultListForm, RESULT_LIST);
		try {
			restTemplate.delete(restUrlResolver.resolveRestUri(RESULT_URI, resultMetricsId.toString()));
		} catch (final BusinessException e) {
			errors.reject(e.getProperties().getKey(),
					e.getProperties().getVars().stream().map(VarValue::getValue).toArray(), e.getMessage());
		} catch (final Exception e) {
			LOG.error(String.format("Failed to delete result list %s", resultMetricsId), e);
			errors.reject(GENERIC_ERROR.getCode(), GENERIC_ERROR.getArguments(), GENERIC_ERROR.getDefaultMessage());
		}
		redirectAttributes.addFlashAttribute("successMessages", ImmutableList
				.of(ImtSuccessMessage.builder().key("result.delete.success").vars(resultMetricsId).build()));
		return REDIRECT_LIST;
	}

	/**
	 * Template pentru incarcarea unui fisier
	 *
	 * @param model
	 *            modelul
	 * @return template-ul
	 */
	@GetMapping(FILE_UPLOAD_PATH)
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
	@PostMapping(FILE_UPLOAD_PATH)
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

}
