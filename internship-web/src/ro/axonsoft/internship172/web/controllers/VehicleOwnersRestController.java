package ro.axonsoft.internship172.web.controllers;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import ro.axonsoft.internship172.api.RoIdCardParser;
import ro.axonsoft.internship172.data.domain.Batch;
import ro.axonsoft.internship172.data.domain.MdfBatch;
import ro.axonsoft.internship172.data.domain.MdfVehicleOwner;
import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.ImtPageCriteria;
import ro.axonsoft.internship172.web.services.VehicleOwnerRestService;

/**
 * Controller pentru partea de in din baza de date
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "/rest/v1/vehicleOwners")
public class VehicleOwnersRestController {

	private static final Logger LOG = LoggerFactory.getLogger(VehicleOwnersRestController.class);

	RoIdCardParser roIdCardParser;

	VehicleOwnerRestService vehicleOwnerService;

	@Inject
	public void setVehicleOwnerService(final VehicleOwnerRestService vehicleOwnerService) {
		this.vehicleOwnerService = vehicleOwnerService;
	}

	@Inject
	public void setRoIdCardParser(final RoIdCardParser roIdCardParser) {
		this.roIdCardParser = roIdCardParser;
	}

	/**
	 * Metoda de gasire a unei inregistrari in baza de date
	 *
	 * @param id
	 *            identificatorul unic
	 * @return cetateanul cautat
	 */
	@RequestMapping(value = "/getVehicleOwner/{id}", method = RequestMethod.GET)
	public @ResponseBody VehicleOwner getVehicleOwner(@PathVariable("id") final Long id)
			throws InvalidDatabaseAccessException {
		return vehicleOwnerService.selectVehicleOwnerById(id);
	}

	/**
	 * Inserarea unui detinator in baza de date
	 *
	 * @param vehicleOwner
	 *            inregistrarea cu datele solicitate
	 * @return inregistrarea finala
	 */
	@RequestMapping(value = "/insertVehicleOwner", method = RequestMethod.POST)
	public ResponseEntity<VehicleOwner> insertVehicleOwner(@RequestBody final MdfVehicleOwner vehicleOwner)
			throws DatabaseIntegrityViolationException {
		LOG.info("inserarea noii inregistrari pe tabela de VEHICLE_OWNER " + vehicleOwner.toString());
		vehicleOwnerService.insertVehicleOwner(vehicleOwner);
		LOG.info("dupa inserare " + vehicleOwner.toString());
		return ResponseEntity.ok(vehicleOwner);
	}

	@RequestMapping(value = "/insertBatch/{pageSize}/{currentPage}", method = RequestMethod.POST)
	public ResponseEntity<MdfBatch> insertBatchOwner(@RequestBody final MdfBatch batch,
			@PathVariable("currentPage") final Integer currentPage, @PathVariable("pageSize") final Integer pageSize) {
		LOG.info("inserarea noii inregistrari pe tabela de BATCH ");
		vehicleOwnerService.insertBatch(batch);
		LOG.info("dupa inserare " + batch.toString());
		return ResponseEntity.ok(batch);
	}

	/**
	 * Stergere inregistrare dupa id
	 *
	 * @param vehicleOwner
	 *            inregistrarea care trebuie stearsa
	 * @param id
	 *            identificator-ul dupa care se face stergerea
	 * @return un obiect de tip vehicle Owner pentru a putea afisa stergerea
	 * @throws DatabaseIntegrityViolationException
	 *             daca id-ul nu este in baza de date
	 */
	@RequestMapping(value = "/deleteVehicleOwnerById/{id}", method = RequestMethod.POST)
	public ResponseEntity<MdfVehicleOwner> delete(@RequestBody final MdfVehicleOwner vehicleOwner,
			@PathVariable("id") final Long id) throws DatabaseIntegrityViolationException {
		vehicleOwnerService.deleteVehicleOwnerById(id);
		LOG.info("am primit data:" + vehicleOwner.toString());
		return ResponseEntity.ok(vehicleOwner);
	}

	/**
	 * Selectarea unei pagini din baza de date
	 *
	 * @param batchId
	 *            batch-ul pentru care se cauta pagina
	 * @param pageSize
	 *            dimensiunea paginii
	 * @param currentPage
	 *            paginea curenta -0
	 * @return lista cu detinatorii de masini de pe pagina selectata
	 * @throws InvalidDatabaseAccessException
	 *             batch-ul este invalid
	 */
	@RequestMapping(value = "/getVehicleOwnersByPage/{batchId}/{pageSize}/{currentPage}", method = RequestMethod.GET)
	public @ResponseBody VehicleOwner[] getAllVehicleOwnerListPage(@PathVariable("batchId") final Long batchId,
			@PathVariable("pageSize") final int pageSize, @PathVariable("currentPage") final int currentPage

	) throws InvalidDatabaseAccessException {
		vehicleOwnerService.selectBatchById(batchId);
		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}

		final Integer vehicleOwnersCount = vehicleOwnerService.countVehicleOwner();
		final int startIndex = currentPage * pageSize;
		List<VehicleOwner> vehicleOwnersList = null;
		if (startIndex < vehicleOwnersCount) {
			vehicleOwnersList = Lists.newArrayList(
					vehicleOwnerService.getVehicleOwnersPage(ImtPageCriteria.of(startIndex, pageSize, batchId)));
		}
		if (vehicleOwnersList == null) {
			throw new InvalidDatabaseAccessException("pagina depaseste datele din baza de date");
		}
		final VehicleOwner[] resultArray = vehicleOwnersList.toArray(new VehicleOwner[vehicleOwnersList.size()]);
		LOG.info("Lista de detinatori de permise pentru batch-ul selectat este" + vehicleOwnersList.toString());
		return resultArray;
	}

	/**
	 * Metoda de extragere al numarului de pagini existente in baza de date
	 *
	 * @param batchId
	 *            batch-ul pentru care se efectueza calculele
	 * @param pageSize
	 *            dimensiunea unei pagini
	 * @return numarul de pagini disponibile
	 * @throws InvalidDatabaseAccessException
	 *             batch-ul nu exista
	 */
	@RequestMapping(value = "/getNumberOfPages/{batchId}/{pageSize}", method = RequestMethod.GET)
	public @ResponseBody Integer getNumberOfPages(@PathVariable("batchId") final Long batchId,
			@PathVariable("pageSize") final int pageSize) throws InvalidDatabaseAccessException {
		vehicleOwnerService.selectBatchById(batchId);

		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}
		final Integer vehicleOwnersCount = vehicleOwnerService.countVehicleOwnersByBatchId(batchId);
		Integer numOfPages = vehicleOwnersCount / pageSize;
		if (vehicleOwnersCount % pageSize != 0) {
			numOfPages++;
		}
		return numOfPages;
	}

	/**
	 * Metoda de extragere a unei pagini de batch-uri
	 *
	 * @param pageSize
	 *            dimensiunea unei pagini
	 * @param currentPage
	 *            pagina curenta
	 * @return o lista cu batch-urile pentru parametrii specificati
	 * @throws InvalidDatabaseAccessException
	 *             dimensiunea unei pagini trebuie se fie mai mare decat 0
	 */

	@RequestMapping(value = "/getBatchByPage/{pageSize}/{currentPage}", method = RequestMethod.GET)
	public @ResponseBody Batch[] getBatchListPage(@PathVariable("pageSize") final int pageSize,
			@PathVariable("currentPage") final int currentPage) throws InvalidDatabaseAccessException {

		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}

		final Integer countedBatches = vehicleOwnerService.countBatches();
		final int startIndex = currentPage * pageSize;
		List<Batch> batchList = null;
		if (startIndex < countedBatches) {
			batchList = Lists
					.newArrayList(vehicleOwnerService.getBatchPage(ImtPageCriteria.of(startIndex, pageSize, 0L)));
		}
		if (batchList == null) {
			throw new InvalidDatabaseAccessException("pagina depaseste datele din baza de date");
		}
		final Batch[] resultArray = batchList.toArray(new Batch[batchList.size()]);
		LOG.info("Lista de detinatori de permise pentru batch-ul selectat este" + batchList.toString());
		return resultArray;
	}

	/**
	 * Metoda de aflare al numarul de pagini pentru tabela de batch in functie de
	 * dimensiunea de paginii specificata
	 *
	 * @param pageSize
	 *            dimensiunea unei pagini
	 * @return numarul de pagini din tabela in functie de dimensiunea unei pagini
	 * @throws InvalidDatabaseAccessException
	 *             dimensiunea nu poate fi 0
	 */
	@RequestMapping(value = "/getNumberOfPagesForBatch/{pageSize}", method = RequestMethod.GET)
	public @ResponseBody Integer getNumberOfPagesForBatch(@PathVariable("pageSize") final int pageSize)
			throws InvalidDatabaseAccessException {

		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}
		final Integer batchesCount = vehicleOwnerService.countBatches();
		Integer numOfPages = batchesCount / pageSize;
		if (batchesCount % pageSize != 0) {
			numOfPages++;
		}
		return numOfPages;
	}

	/**
	 * Lista cu toti detinatorii de permise
	 * @return array cu toate datele din baza de date
	 * @throws InvalidDatabaseAccessException
	 */
	@RequestMapping(value = "/getAllVehicleOwners", method = RequestMethod.GET)
	public @ResponseBody VehicleOwner[] getAllVehicleOwners() throws InvalidDatabaseAccessException {
		final int pageSize = 3;
		Iterable<VehicleOwner> vehicleOwnersRecordsIterable = Lists.newArrayList();
		final Integer vehicleOwnersCount = vehicleOwnerService.countVehicleOwner();
		final Iterable<Batch> batchIdsList = vehicleOwnerService.selectAllBatches();
		for (final Batch e : batchIdsList) {
			for (int startIndex = 0; startIndex < vehicleOwnersCount; startIndex += pageSize) {

				final List<VehicleOwner> vehicleOwnersList = Lists.newArrayList(vehicleOwnerService
						.getVehicleOwnersPage(ImtPageCriteria.of(startIndex, pageSize, e.getBatchId())));
				vehicleOwnersRecordsIterable = Iterables.concat(vehicleOwnersRecordsIterable, vehicleOwnersList);
				LOG.info("Procesarea paginii cu " + vehicleOwnersRecordsIterable.toString());
			}
		}
		final List<VehicleOwner> vehicleOwnersList = Lists.newArrayList(vehicleOwnersRecordsIterable);
		final VehicleOwner[] resultArray = vehicleOwnersList.toArray(new VehicleOwner[vehicleOwnersList.size()]);
		LOG.info("Lista de detinatori de permise pentru batch-ul selectat este" + vehicleOwnersList.toString());
		return resultArray;
	}

	@ExceptionHandler({ DatabaseIntegrityViolationException.class, InvalidDatabaseAccessException.class })
	public String vehicleOwnerExceptionsHandling(final Exception exception) throws Exception {
		throw exception;
	}

	/**
	 * Modelul pentru inserare
	 *
	 * @return un model pentru inserare
	 */
	@RequestMapping("/getVehicleOwner")
	public MdfVehicleOwner vehicleOwner() {
		final MdfVehicleOwner tmp = MdfVehicleOwner.create();
		tmp.setComentariu("");
		tmp.setBatchId(1L);
		tmp.setIssueDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
		tmp.setRegPlate("");
		tmp.setVehicleOwnerId(0L);
		tmp.setRoIdCard("");
		return tmp;
	}

	/**
	 * Verificarea corectitudinii carti de identitate
	 *
	 * @param roIdCard
	 *            string cu cartea de identitate introdusa de utilizator
	 * @return -1 invalid, 0 valid
	 */
	@RequestMapping("/test/{roIdCard}/{batchId}")
	public @ResponseBody Integer setEvent(@PathVariable("roIdCard") final String roIdCard,
			@PathVariable("batchId") final Long batchId) {
		try {
			final List<VehicleOwner> temp = Lists
					.newArrayList(vehicleOwnerService.selectVehicleOwnerByRoIdCard(roIdCard));
			if (temp != null) {
				if (temp.size() != 0) {
					return -1;
				}
				final Batch b = vehicleOwnerService.selectBatchById(batchId);
				if (b == null) {
					System.out.println("In here");
					return -2;
				}
			}
			roIdCardParser.parseIdCard(roIdCard);
		} catch (final Exception e) {
			return -1;
		}
		return 0;
	}
}
