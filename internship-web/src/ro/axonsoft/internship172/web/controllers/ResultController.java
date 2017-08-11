package ro.axonsoft.internship172.web.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.MdfConcreteResultMetrics;

/**
 * Controller pentru procesare sau obtinere rezutlate
 *
 * @author Andra
 *
 */
@Controller
@RequestMapping("/process")
public class ResultController {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessRestController.class);

	private static final String LOCAL_SERVER = LocalHostInit.initLocalHost();

	/**
	 * Metoda de procesare pentru inregistrarile din baza de date
	 *
	 * @param model
	 *            partea grafica pe care se afiseaza rezultatul
	 * @param batchId
	 *            identificatorul pentru care se realizeaza procesarea
	 * @return numele template-ului pe care se afiseaza rezulatele
	 */
	@GetMapping("/databaseProcess/")
	public String processSelectedBatch(final Model model, @RequestParam("batchId") final Long batchId) {

		final RestTemplate restTemplate = new RestTemplate();
		String uri = LOCAL_SERVER + "rest/v1/processing/db/";
		uri += batchId;

		// apeleaza metoda de get de pe controller-ul de rest
		// care proceseaza batch-ul si returneaza rezultatul
		final ResponseEntity<MdfConcreteResultMetrics> temp = restTemplate.getForEntity(uri,
				MdfConcreteResultMetrics.class);
		final MdfConcreteResultMetrics processResult = temp.getBody();
		LOG.info("rezultatul primit  este:" + processResult.toString());
		model.addAttribute("processResult", processResult);

		return "processResult";
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
	@GetMapping("/results/")
	public String getResultPage(final Model model, @RequestParam("batchId") final Long batchId,
			@RequestParam("pageSize") final Integer pageSize, @RequestParam("currentPage") final Integer currentPage)

	{

		final RestTemplate restTemplate = new RestTemplate();
		String uri = LOCAL_SERVER + "rest/v1/results/getResultsByPage/";
		uri += batchId;
		uri += "/";
		uri += pageSize;
		uri += "/";
		uri += currentPage;


		final ResponseEntity<MdfConcreteResultMetrics[]> temp = restTemplate.getForEntity(uri,
				MdfConcreteResultMetrics[].class);
		final MdfConcreteResultMetrics[] processResultArray = temp.getBody();
		LOG.info("rezultatul primit  este:" + processResultArray.toString());
		final List<MdfConcreteResultMetrics> processResultList = Arrays.asList(processResultArray);
		model.addAttribute("resultMetricsList", processResultList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("batchId", batchId);

		uri = LOCAL_SERVER + "rest/v1/results/";
		uri += "getNumberOfPages/";
		uri += batchId;
		uri += "/";
		uri += pageSize;

		final ResponseEntity<Integer> numberOfPages = restTemplate.getForEntity(uri, Integer.class);

		//calcule pentru paginare
		final List<Integer> numOfPagesList = Lists.newArrayList();
		Integer numOfPages = numberOfPages.getBody().intValue();
		int startFrom = currentPage - 2;
		Integer dotsPrev = currentPage - 3;
		Integer dotsNext = currentPage + 4;

		if (dotsPrev < 0) {
			dotsPrev = 0;
		}
		if (startFrom < 0) {
			startFrom = 0;
		}
		int endWith = currentPage + 3;
		if (endWith > numOfPages) {
			endWith = numOfPages;
		}
		if (dotsNext > numOfPages) {
			dotsNext = numOfPages;
		}
		for (int i = startFrom; i < endWith; i++) {
			numOfPagesList.add(i);
		}
		model.addAttribute("numberOfPages", numOfPagesList);

		numOfPages--;
		final List<Integer> currentPageList = Lists.newArrayList(currentPage, dotsPrev, dotsNext, numOfPages);

		model.addAttribute("currentPage", currentPageList);

		return "processResultList";
	}

	/**
	 * Template pentru incarcarea unui fisier
	 * @param model modelul
	 * @return template-ul
	 */
	@GetMapping("/fileUpload")
	public String listUploadedFiles(final Model model) {

		return "fileUpload";
	}

	/**
	 * Metoda de post pentru procesarea unui fisier
	 * @param file fisier-ul incarcat de utilizator
	 * @param model template-ul pe care va fi adaugat rezultatul
	 * @return
	 */
	@PostMapping("/fileUpload")
	public String handleFileUpload(@RequestParam("file") final MultipartFile file, final Model model) {
		final RestTemplate restTemplate = new RestTemplate();
		String uri = LOCAL_SERVER + "rest/v1/processing";
		uri += "/csv/json";

		try {
		    //metoda de transmitere a unui inputStream prin restTemplate
			final InputStream fis = file.getInputStream();
			final RequestCallback requestCallback = request -> {
				request.getHeaders().add("Content-type", "application/octet-stream");
				IOUtils.copy(fis, request.getBody());
			};

			final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setBufferRequestBody(false);
			restTemplate.setRequestFactory(requestFactory);
			final HttpMessageConverterExtractor<MdfConcreteResultMetrics> responseExtractor = new HttpMessageConverterExtractor<>(
					MdfConcreteResultMetrics.class, restTemplate.getMessageConverters());
			final MdfConcreteResultMetrics tmp = restTemplate.execute(uri, HttpMethod.POST, requestCallback,
					responseExtractor);
			model.addAttribute("processResult", tmp);
			return "processResult";

		} catch (IllegalStateException | IOException e) {

		}
		return "processResult";

	}

	/**
	 * Metoda pentru definirea unui template custom in cazul aparitiei unei erori
	 * @param model
	 * @param exception orice exceptie ar putea aparea
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

	/**
	 * Initializare string-ului de prefix pentru metodele de pe rest
	 * @author intern
	 *
	 */
	private static class LocalHostInit {
		private static String initLocalHost() {
			final String props = System.getProperty("ro.axonsoft.internship.local_server");
			checkArgument(props != null, "Serverul local nu poate fi null");
			return props;
		}
	}

}
