package ro.axonsoft.internship172.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ro.axonsoft.internship172.business.api.vehicleOwner.VehicleOwnerBusiness;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.model.base.ImtPagination;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.batch.BatchCreate;
import ro.axonsoft.internship172.model.batch.BatchCreateResult;
import ro.axonsoft.internship172.model.batch.BatchGetResult;
import ro.axonsoft.internship172.model.batch.BatchSortCriterionType;
import ro.axonsoft.internship172.model.batch.ImtBatchGet;
import ro.axonsoft.internship172.model.batch.ImtBatchSortCriterion;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerGet;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerSortCriterion;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerUpdate;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerUpdateProperties;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerSortCriterionType;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerUpdateProperties;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerUpdateResult;
import ro.axonsoft.internship172.web.rest.util.RestUtil;

/**
 * Controller pentru partea de in din baza de date
 *
 * @author intern
 *
 */
@RestController
@RequestMapping(value = "v1/vehicleOwners")
public class VehicleOwnersRestController {

	private static final Logger LOG = LoggerFactory.getLogger(VehicleOwnersRestController.class);

	VehicleOwnerBusiness vhoBusiness;

	@Inject
	public void setVhoBusiness(VehicleOwnerBusiness vhoBusiness) {
		this.vhoBusiness = vhoBusiness;
	}

	@RequestMapping(method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<VehicleOwnerGetResult> getVehicleOwners(@RequestParam(defaultValue = "1") final Integer page,
			@RequestParam(defaultValue = "20") final Integer pageSize,
			@RequestParam(defaultValue = "RO_ID_CARD:ASC") final List<String> sort,
			@RequestParam(value = "search", required = false) final String search) {

		final VehicleOwnerGetResult vhoGetResult = vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet.builder()
				.pagination(ImtPagination.of(page, pageSize)).sort(RestUtil.parseSort(sort,
						VehicleOwnerSortCriterionType.class, (x, y) -> ImtVehicleOwnerSortCriterion.of(x, y)))
				.search(search).build());

		return ResponseEntity.ok(vhoGetResult);
	}

	@RequestMapping(path = "/batches", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<BatchGetResult> getBatches(@RequestParam(defaultValue = "1") final Integer page,
			@RequestParam(defaultValue = "20") final Integer pageSize,
			@RequestParam(defaultValue = "BATCH_ID:ASC") final List<String> sort,
			@RequestParam(value = "search", required = false) final String search) {

		final BatchGetResult batchGetResult = vhoBusiness.getBatches(ImtBatchGet.builder()
				.pagination(ImtPagination.of(page, pageSize))
				.sort(RestUtil.parseSort(sort, BatchSortCriterionType.class, (x, y) -> ImtBatchSortCriterion.of(x, y)))
				.search(search).build());

		return ResponseEntity.ok(batchGetResult);
	}

	@RequestMapping(path = "/{roIdCard}", method = RequestMethod.PUT)
	public ResponseEntity<VehicleOwnerUpdateResult> putVehicleOwner(@PathVariable final String roIdCard,
			@RequestBody final VehicleOwnerUpdateProperties vhoUpdateProperties) throws BusinessException {
		return ResponseEntity.ok(vhoBusiness.updateVehicleOwner(ImtVehicleOwnerUpdate.builder().roIdCard(roIdCard)
				.properties(ImtVehicleOwnerUpdateProperties.builder().comentariu(vhoUpdateProperties.getComentariu())
						.regPlate(vhoUpdateProperties.getRegPlate()).roIdCard(vhoUpdateProperties.getRoIdCard())
						.issueDate(vhoUpdateProperties.getIssueDate()).build())
				.build()));
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<VehicleOwnerCreateResult> postVehicleOwner(@RequestBody final VehicleOwnerCreate vhoCreate)
			throws BusinessException {
		return ResponseEntity.ok(vhoBusiness.createVehicleOwner(vhoCreate));
	}

	/**
	 * Metoda de gasire a unei inregistrari in baza de date
	 *
	 * @param id
	 *            identificatorul unic
	 * @return cetateanul cautat
	 */
	@RequestMapping(value = "/{roIdCard}", method = RequestMethod.GET)
	public @ResponseBody VehicleOwnerBasicRecord getVehicleOwner(@PathVariable("roIdCard") final String roIdCard)
			throws InvalidDatabaseAccessException {
		return vhoBusiness.getVehicleOwners(ImtVehicleOwnerGet.builder()
				.addSort(ImtVehicleOwnerSortCriterion.of(VehicleOwnerSortCriterionType.RO_ID_CARD, SortDirection.ASC))
				.roIdCard(roIdCard).build()).getList().get(0);
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

	@RequestMapping(path = "/create-batch", method = RequestMethod.POST)
	public ResponseEntity<BatchCreateResult> postBatch(@RequestBody final BatchCreate batchCreate)
			throws BusinessException {
		return ResponseEntity.ok(vhoBusiness.createBatch(batchCreate));
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

	@RequestMapping(path = "/{roIdCard}", method = RequestMethod.DELETE)
	public ResponseEntity<VehicleOwnerDeleteResult> deleteUser(@PathVariable final String roIdCard)
			throws BusinessException {
		return ResponseEntity.ok(vhoBusiness.deleteVehicleOwner(roIdCard));
	}

}
