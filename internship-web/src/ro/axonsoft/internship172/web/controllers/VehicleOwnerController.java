package ro.axonsoft.internship172.web.controllers;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.MdfVehicleOwner;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardException;
import ro.axonsoft.internship172.model.base.MdfResultBatch;

/**
 * Controller pentru afisarea si interogarea tabelei de intrare
 *
 * @author Andra
 *
 */

@Controller
public class VehicleOwnerController {
	private static final Logger LOG = LoggerFactory.getLogger(VehicleOwnerController.class);
	private static final String LOCAL_SERVER = LocalHostInit.initLocalHost();

	/**
	 * Metoda de inserare in baza de date
	 *
	 * @param model
	 *            resursa pe care se adauga un obiect de tip vehicle owner
	 * @return numele template-ului folosit
	 */
	@GetMapping("/insertVehicleOwner")
	public String vehicleOwnerForm(final Model model,
			@RequestParam(value = "batchId", required = false) final Long batchId) {
		final RestTemplate restTemplate = new RestTemplate();

		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/getVehicleOwner";
		final MdfVehicleOwner vehicleOwner = restTemplate.getForObject(uri, MdfVehicleOwner.class);
		boolean showBatchField = false;
		if (batchId == null) {
			showBatchField = true;
		} else {
			vehicleOwner.setBatchId(batchId);
			showBatchField = false;
		}
		model.addAttribute("vehicleOwner", vehicleOwner);
		model.addAttribute("showBatchField", showBatchField);
		return "vehicleOwner";

	}

	/**
	 * Metoda post pentru metoda de mai sus
	 *
	 * @param vehicleOwner
	 *            obiectul returnat de pe model care trebuie salvat in baza de date
	 * @return numele template-ului pe care se afiseaza un rezultat
	 * @throws InvalidRoIdCardException
	 *             cartea de identitate specificata poate fi invalida-> arunca
	 *             exceptie
	 */
	@PostMapping("/insertVehicleOwner")
	public String vehicleOwnerSubmit(@ModelAttribute("vehicleOwner") final MdfVehicleOwner vehicleOwner)
			throws InvalidRoIdCardException {
		final RestTemplate restTemplate = new RestTemplate();
		vehicleOwner.setVehicleOwnerId(null);
		final String roIdCard = vehicleOwner.getRoIdCard();
		if (roIdCard == null || roIdCard.isEmpty()) {
			vehicleOwner.setRoIdCard("Invalid Id Card");
			return "vehicleOwner";
		}

		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/insertVehicleOwner";

		restTemplate.postForEntity(uri, vehicleOwner, MdfVehicleOwner.class);

		return "vehicleOwnerInsertResult";
	}

	/**
	 * Mapare pentru inserarea unui batch
	 *
	 * @param model
	 * @param pageSize
	 *            dimensiunea paginii in momentul inserarii
	 * @param currentPage
	 *            pagina pe care se aflta utilizatorul
	 * @return lista
	 */
	@GetMapping("/insertBatch/")
	public String insertBatch(final Model model, @RequestParam("pageSize") final Integer pageSize,
			@RequestParam("currentPage") final Integer currentPage) {
		final RestTemplate restTemplate = new RestTemplate();

		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/insertBatch";
		uri += "/";
		uri += pageSize;
		uri += "/";
		uri += currentPage;
		restTemplate.postForEntity(uri, MdfResultBatch.create(), MdfResultBatch.class);

		return getBatchListPage(model, currentPage, pageSize);
	}

	/**
	 * Metoda de stergere a unei inregistrari
	 *
	 * @param vehicleOwnerId
	 *            identificatorul dupa care se face stergerea
	 * @param vehicleOwner
	 *            inregistrarea stearsa
	 * @return numele de template-ului de afisare
	 */
	@PostMapping("/deleteVehicleOwner/{roIdCard}")
	public String vehicleOwnerDelete(@PathVariable("roIdCard") final String roIdCard,
			@ModelAttribute("vehicleOwner") final MdfVehicleOwner vehicleOwner) {
		final RestTemplate restTemplate = new RestTemplate();
		LOG.info("se sterge " + vehicleOwner.toString());

		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/deleteVehicleOwnerByRoIdCard";
		uri += "/";
		uri += roIdCard;

		restTemplate.postForEntity(uri, vehicleOwner, MdfVehicleOwner.class);
		return "deleteVehicleOwner";
	}

	/**
	 * Mapare pentru toate batch-urile
	 *
	 * @param model
	 * @return
	 */
	@GetMapping("/batches")
	public String getBatchList(final Model model) {
		final RestTemplate restTemplate = new RestTemplate();

		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/getAllBatches";
		final ResponseEntity<MdfResultBatch[]> batchResponse = restTemplate.getForEntity(uri, MdfResultBatch[].class);
		LOG.info("Raspuns returnata" + batchResponse.toString());
		final MdfResultBatch[] batchArray = batchResponse.getBody();
		final List<MdfResultBatch> batchList = Arrays.asList(batchArray);
		LOG.info("Raspuns returnata" + batchList.toString());
		model.addAttribute("batchList", batchList);

		return "batchList";
	}

	/**
	 * Maparea liste de inregistrari de intrare
	 *
	 * @param model
	 *            pe care se adauga lista
	 * @param batchId
	 *            identificatorul dupa care se cauta rezulatele in baza de date
	 * @param pageSize
	 *            dimensiunea unei paginii de rezultate
	 * @param currentPage
	 *            pagina curenta
	 * @return numele template-ului
	 */

	@GetMapping("/vehicleOwners/")
	public String getVehicleOwnersListByBatchId(final Model model, @RequestParam("batchId") final Long batchId,
			@RequestParam("pageSize") final Integer pageSize, @RequestParam("currentPage") final Integer currentPage) {
		final RestTemplate restTemplate = new RestTemplate();
		;

		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/getVehicleOwnersByPage/" + batchId;
		uri += "/";
		uri += pageSize;
		uri += "/";
		uri += currentPage;

		final ResponseEntity<MdfVehicleOwner[]> vehicleOwnerResponse = restTemplate.getForEntity(uri,
				MdfVehicleOwner[].class);
		LOG.info("Raspuns returnat ca entitate" + vehicleOwnerResponse.toString());
		final MdfVehicleOwner[] vehicleOwnersArray = vehicleOwnerResponse.getBody();
		final List<MdfVehicleOwner> vehicleOwnersList = Arrays.asList(vehicleOwnersArray);
		LOG.info("Raspuns returnat ca lista" + vehicleOwnersList.toString());
		model.addAttribute("vehicleOwnersList", vehicleOwnersList);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("batchId", batchId);
		uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/";
		uri += "getNumberOfPages/";
		uri += batchId;
		uri += "/";
		uri += pageSize;

		final ResponseEntity<Integer> numberOfPages = restTemplate.getForEntity(uri, Integer.class);

		Integer numOfPages = numberOfPages.getBody().intValue();
		final List<Integer> numOfPagesList = Lists.newArrayList();
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

		model.addAttribute("numberOfPages", numOfPagesList);
		return "vehicleOwnersList";
	}

	/**
	 * Afisarea tuturor batch-urilor
	 *
	 * @param model
	 *            modelul pe care se adauga lista
	 * @param currentPage
	 *            pagina curenta default - 0
	 * @param pageSize
	 *            dimensiunea unei pagini
	 * @return lista cu batch-urile
	 */
	@GetMapping("/batches/")
	public String getBatchListPage(final Model model, @RequestParam("currentPage") final Integer currentPage,
			@RequestParam("pageSize") final Integer pageSize) {
		final RestTemplate restTemplate = new RestTemplate();
		String uri = LOCAL_SERVER;
		uri += "rest/v1/vehicleOwners/getBatchByPage";
		uri += "/";
		uri += pageSize;
		uri += "/";
		uri += currentPage;
		final ResponseEntity<MdfResultBatch[]> batchResponse = restTemplate.getForEntity(uri, MdfResultBatch[].class);
		LOG.info("Raspuns returnat" + batchResponse.toString());
		final MdfResultBatch[] batchArray = batchResponse.getBody();
		final List<MdfResultBatch> batchList = Arrays.asList(batchArray);
		LOG.info("Raspuns returnat" + batchList.toString());
		model.addAttribute("batchList", batchList);
		model.addAttribute("pageSize", pageSize);

		uri = LOCAL_SERVER + "rest/v1/vehicleOwners";
		uri += "/getNumberOfPagesForBatch";
		uri += "/";
		uri += pageSize;

		final ResponseEntity<Integer> numberOfPages = restTemplate.getForEntity(uri, Integer.class);

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
		final List<Integer> currentPageList = Lists.newArrayList(currentPage, dotsPrev, dotsNext, numOfPages, pageSize);

		model.addAttribute("currentPage", currentPageList);

		return "batchList";
	}

	/**
	 * Pagina de home
	 *
	 * @param model
	 * @return
	 */

	@GetMapping("/home")
	public String getHomePage(final Model model) {
		return "home";
	}

	/**
	 * Exceptiile care pot aparea
	 *
	 * @param model
	 *            pe care adaugam
	 * @param exception
	 * @return
	 */
	@ExceptionHandler({ Exception.class })
	public String vehicleOwnerExceptionsHandling(final Model model, final Exception exception) {
		String msg = " ";
		if (exception.getMessage() != null) {
			msg += exception.getMessage();
		}
		model.addAttribute("error", exception.getClass().toString() + msg);
		exception.printStackTrace();
		return "exception";
	}

	private static class LocalHostInit {
		private static String initLocalHost() {
			final String props = System.getProperty("ro.axonsoft.internship.local_server");
			checkArgument(props != null, "Serverul local nu poate fi null");
			return props;
		}
	}
}
