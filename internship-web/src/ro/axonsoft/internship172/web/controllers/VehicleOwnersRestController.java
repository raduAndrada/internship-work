package ro.axonsoft.internship172.web.controllers;

import java.sql.Date;
import java.time.Instant;
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

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.business.api.vehicleOwner.VehicleOwnerBusiness;
import ro.axonsoft.internship172.data.domain.ImtVehicleOwner;
import ro.axonsoft.internship172.data.domain.MdfVehicleOwner;
import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.ImtResultBatch;
import ro.axonsoft.internship172.model.base.MdfResultBatch;
import ro.axonsoft.internship172.model.base.ResultBatch;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.batch.BatchCreateResult;
import ro.axonsoft.internship172.model.batch.BatchGetResult;
import ro.axonsoft.internship172.model.batch.BatchSortCriterionType;
import ro.axonsoft.internship172.model.batch.ImtBatchCreate;
import ro.axonsoft.internship172.model.batch.ImtBatchGet;
import ro.axonsoft.internship172.model.batch.ImtBatchSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerGet;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerSortCriterionType;

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

	VehicleOwnerBusiness vhoBusiness;

	@Inject
	public void setVhoBusiness(VehicleOwnerBusiness vhoBusiness) {
		this.vhoBusiness = vhoBusiness;
	}

	/**
	 * Metoda de gasire a unei inregistrari in baza de date
	 *
	 * @param id
	 *            identificatorul unic
	 * @return cetateanul cautat
	 */
	@RequestMapping(value = "/getVehicleOwner/{id}", method = RequestMethod.GET)
	public @ResponseBody VehicleOwnerBasic getVehicleOwner(@PathVariable("id") final Long id)
			throws InvalidDatabaseAccessException {
		return vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet.builder()
				.addSort(ImtVehicleOwnerSortCriterion.of(VehicleOwnerSortCriterionType.RO_ID_CARD, SortDirection.ASC))
				.vehicleOwnerId(id).build()).getList().get(0).getBasic();
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

		String issueDate = vehicleOwner.getIssueDate().toString();
		issueDate += "T11:59:59.59Z";
		final VehicleOwnerCreateResult vhoCreateResult = vhoBusiness.createVehicleOwner(ImtVehicleOwnerCreate.builder()
				.basic(ImtVehicleOwnerBasic.builder().regPlate(vehicleOwner.getRegPlate())
						.roIdCard(vehicleOwner.getRoIdCard()).issueDate(Instant.parse(issueDate))
						.comentariu(vehicleOwner.getComentariu()).build())
				.batch(ImtResultBatch.builder().batchId(vehicleOwner.getBatchId()).build()).build());
		LOG.info("inserarea noii inregistrari pe tabela de VEHICLE_OWNER " + vehicleOwner.toString());
		// vehicleOwnerService.insertVehicleOwner(vehicleOwner);
		LOG.info("dupa inserare " + vhoCreateResult.toString());
		final VehicleOwner vh = ImtVehicleOwner.builder().batchId(vehicleOwner.getBatchId())
				.comentariu(vhoCreateResult.getBasic().getComentariu())
				.issueDate(new Date(vhoCreateResult.getBasic().getIssueDate().toEpochMilli()))
				.regPlate(vhoCreateResult.getBasic().getRegPlate()).roIdCard(vhoCreateResult.getBasic().getRoIdCard())
				.build();

		return ResponseEntity.ok(vh);
	}

	/**
	 * inserarea unui batch nou
	 *
	 * @param batch
	 *            batch-ul ce va fi adaugat
	 * @param currentPage
	 *            pagina curenta
	 * @param pageSize
	 *            dimensiunea paginii curente
	 * @return un template cu locul unde a ramas utilizatorul
	 */

	@RequestMapping(value = "/insertBatch/{pageSize}/{currentPage}", method = RequestMethod.POST)
	public ResponseEntity<MdfResultBatch> insertBatchOwner(@RequestBody final MdfResultBatch batch,
			@PathVariable("currentPage") final Integer currentPage, @PathVariable("pageSize") final Integer pageSize) {
		LOG.info("inserarea noii inregistrari pe tabela de BATCH ");
		final BatchCreateResult batchCreateResult = vhoBusiness
				.createBatch(ImtBatchCreate.builder().batch(batch).build());
		return ResponseEntity.ok(MdfResultBatch.create().setBatchId(batchCreateResult.getBatch().getBatchId()));
	}

	/**
	 * Stergere inregistrare dupa carte de identitate
	 *
	 * @param vehicleOwner
	 *            inregistrarea care trebuie stearsa
	 * @param id
	 *            identificator-ul dupa care se face stergerea
	 * @return un obiect de tip vehicle Owner pentru a putea afisa stergerea
	 * @throws DatabaseIntegrityViolationException
	 *             daca id-ul nu este in baza de date
	 */
	@RequestMapping(value = "/deleteVehicleOwnerByRoIdCard/{roIdCard}", method = RequestMethod.POST)
	public ResponseEntity<MdfVehicleOwner> delete(@RequestBody final MdfVehicleOwner vehicleOwner,
			@PathVariable("roIdCard") final String roIdCard) throws DatabaseIntegrityViolationException {

		final VehicleOwnerDeleteResult vhoDeleteResult = vhoBusiness.deleteVehicleOwner(roIdCard);
		return ResponseEntity.ok(MdfVehicleOwner.create().setComentariu(vhoDeleteResult.getBasic().getComentariu())
				.setRegPlate(vhoDeleteResult.getBasic().getRegPlate())
				.setRoIdCard(vhoDeleteResult.getBasic().getRoIdCard())
				.setIssueDate(new Date(vhoDeleteResult.getBasic().getIssueDate().getEpochSecond())));
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
		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}
		final VehicleOwnerGetResult vhoGetResult = vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet
				.builder().addSort(ImtVehicleOwnerSortCriterion.builder()
						.criterion(VehicleOwnerSortCriterionType.RO_ID_CARD).direction(SortDirection.ASC).build())
				.batchId(batchId).build());

		final Integer vehicleOwnersCount = vhoGetResult.getCount();
		final int startIndex = currentPage * pageSize;
		final List<VehicleOwner> vehicleOwnersList = Lists.newArrayList();
		VehicleOwnerGetResult vhoGetResultTemp = null;
		if (startIndex < vehicleOwnersCount) {
			vhoGetResultTemp = vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet.builder()
					.addSort(ImtVehicleOwnerSortCriterion.builder().criterion(VehicleOwnerSortCriterionType.RO_ID_CARD)
							.direction(SortDirection.ASC).build())
					.batchId(batchId)
					.pagination(ImtPagination.builder().offset(new Long(startIndex)).pageSize(pageSize).build())
					.batchId(batchId).build());
			vhoGetResultTemp.getList().forEach(e -> {
				vehicleOwnersList.add(ImtVehicleOwner.builder().batchId(e.getBatch().getBatchId())
						.roIdCard(e.getBasic().getRoIdCard()).regPlate(e.getBasic().getRegPlate())
						.comentariu(e.getBasic().getComentariu())
						.issueDate(new Date(e.getBasic().getIssueDate().toEpochMilli())).build());
			});

		}
		final VehicleOwner[] resultArray = vehicleOwnersList.toArray(new VehicleOwner[vehicleOwnersList.size()]);
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
		final VehicleOwnerGetResult vhoGetResult = vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet
				.builder().addSort(ImtVehicleOwnerSortCriterion.builder()
						.criterion(VehicleOwnerSortCriterionType.RO_ID_CARD).direction(SortDirection.ASC).build())
				.batchId(batchId).build());
		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}
		final Integer vehicleOwnersCount = vhoGetResult.getCount();
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
	public @ResponseBody ResultBatch[] getBatchListPage(@PathVariable("pageSize") final int pageSize,
			@PathVariable("currentPage") final int currentPage) throws InvalidDatabaseAccessException {

		if (pageSize == 0) {
			throw new InvalidDatabaseAccessException("dimensiunea unei pagini trebuie sa fie mai mare decat 0");
		}

		final BatchGetResult batchGetResult = vhoBusiness.getBatches(ImtBatchGet.builder()
				.addSort(ImtBatchSortCriterion.of(BatchSortCriterionType.BATCH_ID, SortDirection.ASC)).build());
		final Integer countedBatches = batchGetResult.getCount();
		final int startIndex = currentPage * pageSize;
		final List<ResultBatch> batchList = Lists.newArrayList();
		if (startIndex < countedBatches) {
			final BatchGetResult batchGetResultTemp = vhoBusiness.getBatches(ImtBatchGet.builder()
					.addSort(ImtBatchSortCriterion.of(BatchSortCriterionType.BATCH_ID, SortDirection.ASC))
					.pagination(ImtPagination.builder().offset(new Long(startIndex)).pageSize(pageSize).build())
					.build());
			batchGetResultTemp.getList().forEach(e -> {
				batchList.add(MdfResultBatch.create().setBatchId(e.getBatchId()));

			});

		}
		final ResultBatch[] resultArray = batchList.toArray(new ResultBatch[batchList.size()]);
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

		final BatchGetResult batchGetResult = vhoBusiness.getBatches(ImtBatchGet.builder().addSort(ImtBatchSortCriterion
				.builder().criterion(BatchSortCriterionType.BATCH_ID).direction(SortDirection.ASC).build()).build());
		final Integer batchesCount = batchGetResult.getCount();
		Integer numOfPages = batchesCount / pageSize;
		if (batchesCount % pageSize != 0) {
			numOfPages++;
		}
		return numOfPages;
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
		tmp.setIssueDate(new Date(System.currentTimeMillis()));
		tmp.setRegPlate("");
		tmp.setVehicleOwnerId(0L);
		tmp.setRoIdCard("");
		return tmp;
	}

}
