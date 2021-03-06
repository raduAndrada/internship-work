package ro.axonsoft.internship172.web.controllers;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.base.ImtBatch;
import ro.axonsoft.internship172.model.batch.BatchCreate;
import ro.axonsoft.internship172.model.batch.BatchGetResult;
import ro.axonsoft.internship172.model.batch.ImtBatchCreate;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorProperties.VarValue;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerUpdateProperties;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGetResult;
import ro.axonsoft.internship172.web.model.BatchListForm;
import ro.axonsoft.internship172.web.model.ImtSuccessMessage;
import ro.axonsoft.internship172.web.model.VehicleOwnerForm;
import ro.axonsoft.internship172.web.model.VehicleOwnerListForm;
import ro.axonsoft.internship172.web.util.RestUrlResolver;

/**
 * Controller pentru afisarea si interogarea tabelei de intrare
 *
 * @author Andra
 *
 */
@RequestMapping(VehicleOwnerController.URL_BASE)
@Controller
public class VehicleOwnerController {
	public static final String URL_BASE = "/app";
	public static final String URL_BATCHES = "/batches/";
	public static final String URL_VEHICLE_OWNERS = "/vehicleOwners/";

	private static final String HOME = "/home";
	private static final String RO_ID_CARD = "roIdCard";
	private static final String CREATE_PATH = "/create";
	private static final String CREATE_PATH_BATCH = "/create-batch";
	private static final String RO_ID_CARD_PATH = "/{roIdCard}";
	private static final String DELETE_PATH = "/delete" + RO_ID_CARD_PATH;
	private static final String MODE = "mode";
	private static final String CREATE_MODE = "create";
	private static final String UPDATE_MODE = "update";
	private static final String PAGE_SIZE = "pageSize";
	private static final String PAGE = "page";
	private static final String SEARCH = "search";
	private static final String START = "start";
	private static final String END = "end";
	private static final String START_INDEX = "startIndex";
	private static final String END_INDEX = "endIndex";
	private static final String DOTS_NEXT = "dotsNext";
	private static final String DOTS_PREV = "dotsPrev";

	private static final String BATCH_LIST = "batchList";
	private static final String VEHICLE_OWNER_LIST = "vehicleOwnerList";
	private static final String PAGE_COUNT = "pageCount";
	private static final String VEHICLE_OWNER_FORM_REDIRECT_ERRORS = "vehicleOwnerFormRedirectErrors";
	private static final String VEHICLE_OWNER_FORM = "vehicleOwnerForm";
	private static final String VEHICLE_OWNER_DETAILS_VIEW = "vehicle-owner-details";
	private static final String VEHICLE_OWNER_LIST_VIEW = "vehicle-owner-list";
	private static final String BATCH_LIST_VIEW = "batch-list";
	private static final String REDIRECT_VEHICLE_OWNER_DETAILS_UPDATE = "redirect:" + URL_BASE + RO_ID_CARD_PATH;
	private static final String REDIRECT_VEHICLE_OWNER_DETAILS_CREATE = "redirect:" + URL_BASE + CREATE_PATH;
	private static final String REDIRECT_LIST = "redirect:" + URL_BASE + URL_BATCHES;

	private static final String VEHICLE_OWNER_URI = "vehicleOwners";
	private static final String VEHICLE_OWNER_BATCH_LIST_URI = "vehicleOwners/batches";
	private static final String VEHICLE_OWNER_BATCH_CREATE_URI = "vehicleOwners/create-batch";

	private static final ObjectError GENERIC_ERROR = new ObjectError(VEHICLE_OWNER_FORM,
			new String[] { "generic.tech-error" }, null, "A technical error has occured");

	private static final Logger LOG = LoggerFactory.getLogger(VehicleOwnerController.class);

	@Inject
	private RestTemplate restTemplate;
	@Inject
	private RestUrlResolver restUrlResolver;

	@ModelAttribute("batchListForm")
	public BatchListForm getBatchListForm() {
		return new BatchListForm();
	}

	@RequestMapping(value = URL_BATCHES, method = RequestMethod.GET)
	public String list(@ModelAttribute final BatchListForm batchListForm, final BindingResult bindingResult,
			final ModelMap modelMap) {
		ResponseEntity<BatchGetResult> batchGetResult = null;
		try {
			final UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromUri(restUrlResolver.resolveRestUri(VEHICLE_OWNER_BATCH_LIST_URI))
					.queryParam(PAGE_SIZE, Optional.ofNullable(batchListForm.getPageSize()).orElse(10))
					.queryParam(PAGE, Optional.ofNullable(batchListForm.getPage()).orElse(1))
					.queryParam(SEARCH, batchListForm.getSearch());
			LOG.info(uriBuilder.toString());
			batchGetResult = restTemplate.getForEntity(uriBuilder.build().toUri(), BatchGetResult.class);
		} catch (final Exception e) {
			LOG.error("Failed to fetch users list", e);
			bindingResult.addError(GENERIC_ERROR);
		}
		final Integer currentPage = (batchListForm.getPage() != null) ? batchListForm.getPage() : 1;
		final Integer startIndex = (currentPage - 3 > 1) ? currentPage : 1;
		final boolean start = (startIndex > 3) ? true : false;
		final boolean dotsPrev = (startIndex > 4) ? true : false;
		final Integer endIndex = (currentPage + 3 < batchGetResult.getBody().getPageCount() ? currentPage + 3
				: batchGetResult.getBody().getPageCount());
		final boolean end = (currentPage < batchGetResult.getBody().getPageCount() - 3 ? true : false);
		final boolean dotsNext = (currentPage < batchGetResult.getBody().getPageCount() - 4 ? true : false);

		if (batchGetResult != null && batchGetResult.getStatusCode() == HttpStatus.OK) {
			modelMap.put(BATCH_LIST, batchGetResult.getBody().getList());
			modelMap.put(PAGE_COUNT, batchGetResult.getBody().getPageCount());
			modelMap.put(PAGE_SIZE, batchGetResult.getBody().getPagination().getPageSize());
			modelMap.put(START, start);
			modelMap.put(END, end);
			modelMap.put(START_INDEX, startIndex);
			modelMap.put(END_INDEX, endIndex);
			modelMap.put(DOTS_NEXT, dotsNext);
			modelMap.put(DOTS_PREV, dotsPrev);
		} else {
			modelMap.put(BATCH_LIST, ImmutableList.of());
			modelMap.put(PAGE_COUNT, 0);
			modelMap.put(PAGE_SIZE, 10);
			modelMap.put(START, start);
			modelMap.put(END, end);
			modelMap.put(START_INDEX, startIndex);
			modelMap.put(END_INDEX, endIndex);
			modelMap.put(DOTS_NEXT, dotsNext);
			modelMap.put(DOTS_PREV, dotsPrev);
		}
		return BATCH_LIST_VIEW;
	}

	@RequestMapping(path = CREATE_PATH, method = RequestMethod.GET)
	public String getCreate(@ModelAttribute final VehicleOwnerForm ownerForm, final BindingResult bindingResult,
			final ModelMap modelMap) {
		modelMap.put(MODE, CREATE_MODE);
		if (modelMap.containsAttribute(VEHICLE_OWNER_FORM_REDIRECT_ERRORS)) {
			bindingResult.addAllErrors((Errors) modelMap.get(VEHICLE_OWNER_FORM_REDIRECT_ERRORS));
		}
		return VEHICLE_OWNER_DETAILS_VIEW;
	}

	@RequestMapping(path = CREATE_PATH, method = RequestMethod.POST)
	public String postCreate(@RequestParam @ModelAttribute final String mode,
			@ModelAttribute @Valid final VehicleOwnerForm ownerForm, final BindingResult bindingResult,
			final ModelMap modelMap, final RedirectAttributes redirectAttributes) {
		return save(mode, ownerForm, bindingResult, modelMap, redirectAttributes);
	}

	@RequestMapping(path = RO_ID_CARD_PATH, method = RequestMethod.GET)
	public String getUpdate(@PathVariable final String roIdCard, @ModelAttribute final VehicleOwnerForm ownerForm,
			final BindingResult bindingResult, final ModelMap modelMap) {

		if (modelMap.containsAttribute(VEHICLE_OWNER_FORM_REDIRECT_ERRORS)) {
			bindingResult.addAllErrors((Errors) modelMap.get(VEHICLE_OWNER_FORM_REDIRECT_ERRORS));
		}
		final ResponseEntity<VehicleOwnerBasicRecord> vhoRecord = restTemplate.getForEntity(
				restUrlResolver.resolveRestUri(VEHICLE_OWNER_URI, roIdCard), VehicleOwnerBasicRecord.class);
		ownerForm.setComentariu(vhoRecord.getBody().getBasic().getComentariu());
		ownerForm.setIssueDate(vhoRecord.getBody().getBasic().getIssueDate().toString());
		ownerForm.setRegPlate(vhoRecord.getBody().getBasic().getRegPlate());
		ownerForm.setBatchId(vhoRecord.getBody().getBatch().getBatchId());
		ownerForm.setRoIdCard(vhoRecord.getBody().getBasic().getRoIdCard());
		modelMap.put(MODE, UPDATE_MODE);

		return VEHICLE_OWNER_DETAILS_VIEW;
	}

	@RequestMapping(path = RO_ID_CARD_PATH, method = RequestMethod.POST)
	public String postUpdate(@PathVariable final String roIdCard, @RequestParam @ModelAttribute final String mode,
			@ModelAttribute @Valid final VehicleOwnerForm ownerForm, final BindingResult bindingResult,
			final ModelMap modelMap, final RedirectAttributes redirectAttributes) {
		return save(mode, ownerForm, bindingResult, modelMap, redirectAttributes);
	}

	@RequestMapping(path = CREATE_PATH_BATCH, method = RequestMethod.POST)
	public String postBatchCreate(final ModelMap modelMap, final RedirectAttributes redirectAttributes) {

		try {
			final BatchCreate batchCreate = ImtBatchCreate.builder().batch(ImtBatch.builder().build()).build();
			LOG.info(batchCreate.toString());
			restTemplate.postForLocation(restUrlResolver.resolveRestUri(VEHICLE_OWNER_BATCH_CREATE_URI), batchCreate);
			redirectAttributes.addFlashAttribute("successMessages", ImmutableList.of(ImtSuccessMessage.builder()
					.key(String.format("vehicle-owners.success", "")).vars("batch").build()));
		} catch (final BusinessException e) {
			LOG.debug(String.format("Business error while creating new batch"), e);

		}
		return REDIRECT_LIST;
		// return save(mode, ownerForm, bindingResult, modelMap, redirectAttributes);
	}

	@RequestMapping(path = DELETE_PATH, method = RequestMethod.POST)
	public String postDelete(@PathVariable final String roIdCard, final ModelMap modelMap,
			final RedirectAttributes redirectAttributes) {
		final VehicleOwnerForm ownerForm = new VehicleOwnerForm();
		final Errors errors = new BeanPropertyBindingResult(ownerForm, VEHICLE_OWNER_FORM);
		try {
			restTemplate.delete(restUrlResolver.resolveRestUri(VEHICLE_OWNER_URI, roIdCard));
		} catch (final BusinessException e) {
			errors.reject(e.getProperties().getKey(),
					e.getProperties().getVars().stream().map(VarValue::getValue).toArray(), e.getMessage());
		} catch (final Exception e) {
			LOG.error(String.format("Failed to delete user list %s", roIdCard), e);
			errors.reject(GENERIC_ERROR.getCode(), GENERIC_ERROR.getArguments(), GENERIC_ERROR.getDefaultMessage());
		}
		if (errors.hasErrors()) {
			return redirectOnErrors(UPDATE_MODE, ownerForm, errors, redirectAttributes);
		}
		redirectAttributes.addFlashAttribute("successMessages",
				ImmutableList.of(ImtSuccessMessage.builder().key("users.delete.success").vars(roIdCard).build()));
		redirectAttributes.addAttribute(RO_ID_CARD, ownerForm.getRoIdCard());
		return REDIRECT_LIST;
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

	@GetMapping(URL_VEHICLE_OWNERS)
	public String getVehicleOwnersListByBatchId(@ModelAttribute final VehicleOwnerListForm vehicleOwnerListForm,
			final BindingResult bindingResult, final ModelMap modelMap) {
		final RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<VehicleOwnerGetResult> vehicleOwnerGetResult = null;
		try {
			final UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromUri(restUrlResolver.resolveRestUri(VEHICLE_OWNER_URI))
					.queryParam(PAGE_SIZE, Optional.ofNullable(vehicleOwnerListForm.getPageSize()).orElse(10))
					.queryParam(PAGE, Optional.ofNullable(vehicleOwnerListForm.getPage()).orElse(1))
					.queryParam(SEARCH, vehicleOwnerListForm.getSearch());
			LOG.info(uriBuilder.toString());
			vehicleOwnerGetResult = restTemplate.getForEntity(uriBuilder.build().toUri(), VehicleOwnerGetResult.class);
		} catch (final Exception e) {
			LOG.error("Failed to fetch users list", e);
			bindingResult.addError(GENERIC_ERROR);
		}
		final Integer currentPage = (vehicleOwnerListForm.getPage() != null) ? vehicleOwnerListForm.getPage() : 1;
		final Integer startIndex = (currentPage - 3 > 1) ? currentPage : 1;
		final boolean start = (startIndex > 3) ? true : false;
		final boolean dotsPrev = (startIndex > 4) ? true : false;
		final Integer endIndex = (currentPage + 3 < vehicleOwnerGetResult.getBody().getPageCount() ? currentPage + 3
				: vehicleOwnerGetResult.getBody().getPageCount());
		final boolean end = (currentPage < vehicleOwnerGetResult.getBody().getPageCount() - 3 ? true : false);
		final boolean dotsNext = (currentPage < vehicleOwnerGetResult.getBody().getPageCount() - 4 ? true : false);

		if (vehicleOwnerGetResult != null && vehicleOwnerGetResult.getStatusCode() == HttpStatus.OK) {
			modelMap.put(VEHICLE_OWNER_LIST, vehicleOwnerGetResult.getBody().getList());
			modelMap.put(PAGE_COUNT, vehicleOwnerGetResult.getBody().getPageCount());
			modelMap.put(PAGE_SIZE, vehicleOwnerGetResult.getBody().getPagination().getPageSize());
			modelMap.put(START, start);
			modelMap.put(END, end);
			modelMap.put(START_INDEX, startIndex);
			modelMap.put(END_INDEX, endIndex);
			modelMap.put(DOTS_NEXT, dotsNext);
			modelMap.put(DOTS_PREV, dotsPrev);
		} else {
			modelMap.put(VEHICLE_OWNER_LIST, ImmutableList.of());
			modelMap.put(PAGE_COUNT, 0);
			modelMap.put(PAGE_SIZE, 10);
			modelMap.put(START, start);
			modelMap.put(END, end);
			modelMap.put(START_INDEX, startIndex);
			modelMap.put(END_INDEX, endIndex);
			modelMap.put(DOTS_NEXT, dotsNext);
			modelMap.put(DOTS_PREV, dotsPrev);
		}
		return VEHICLE_OWNER_LIST_VIEW;
	}

	@GetMapping(HOME)
	public String getHomePage(final Model model) {
		return HOME;
	}

	private String save(final String mode, final VehicleOwnerForm ownerForm, final BindingResult bindingResult,
			final ModelMap modelMap, final RedirectAttributes redirectAttributes) {
		modelMap.put(MODE, mode);
		if (bindingResult.hasErrors()) {
			return redirectOnErrors(mode, ownerForm, bindingResult, redirectAttributes);
		} else {
			if (invokeRestSave(mode, ownerForm, bindingResult)) {
				redirectAttributes.addFlashAttribute("successMessages", ImmutableList.of(ImtSuccessMessage.builder()
						.key(String.format("vehicle-owners.%s.success", mode)).vars(ownerForm.getRoIdCard()).build()));
				redirectAttributes.addAttribute(RO_ID_CARD, ownerForm.getRoIdCard());
				return REDIRECT_VEHICLE_OWNER_DETAILS_UPDATE;
			} else {
				return redirectOnErrors(mode, ownerForm, bindingResult, redirectAttributes);
			}
		}
	}

	private boolean invokeRestSave(final String mode, final VehicleOwnerForm ownerForm,
			final BindingResult bindingResult) {
		if (UPDATE_MODE.equals(mode)) {
			try {
				restTemplate.put(restUrlResolver.resolveRestUri(VEHICLE_OWNER_URI, ownerForm.getRoIdCard()),
						ImtVehicleOwnerUpdateProperties.builder().roIdCard(ownerForm.getRoIdCard())
								.regPlate(ownerForm.getRegPlate()).comentariu(ownerForm.getComentariu())
								.issueDate(Instant.parse(ownerForm.getIssueDate() + "T11:59:59.59Z")).build());
			} catch (final BusinessException e) {
				bindingResult.addError(new ObjectError(VEHICLE_OWNER_FORM, new String[] { e.getProperties().getKey() },
						e.getProperties().getVars().stream().map(VarValue::getValue).toArray(), e.getMessage()));
			} catch (final Exception e) {
				LOG.error(String.format("Failed to update user with input %s", ownerForm), e);
				bindingResult.addError(GENERIC_ERROR);
			}
		} else if (CREATE_MODE.equals(mode)) {
			try {
				final VehicleOwnerCreate vhoCreate = ImtVehicleOwnerCreate.builder()
						.basic(ImtVehicleOwnerBasic.builder().roIdCard(ownerForm.getRoIdCard())
								.regPlate(ownerForm.getRegPlate()).comentariu(ownerForm.getComentariu())
								.issueDate(Instant.parse(ownerForm.getIssueDate() + "T11:59:59.59Z")).build())
						.batch(ImtBatch.builder().batchId(ownerForm.getBatchId()).build()).build();
				LOG.info(vhoCreate.toString());
				restTemplate.postForLocation(restUrlResolver.resolveRestUri(VEHICLE_OWNER_URI), vhoCreate);

			} catch (final BusinessException e) {
				LOG.debug(String.format("Business error while creating new user with input %s", ownerForm), e);
				bindingResult.addError(new ObjectError(VEHICLE_OWNER_FORM, new String[] { e.getProperties().getKey() },
						e.getProperties().getVars().stream().map(VarValue::getValue).toArray(), e.getMessage()));
			} catch (final Exception e) {
				LOG.error(String.format("Failed to create new vehicle owner with input %s", ownerForm), e);
				bindingResult.addError(GENERIC_ERROR);
			}
		} else {
			LOG.error(String.format("Unexpected mode %s", mode));
			bindingResult.addError(GENERIC_ERROR);
		}
		return !bindingResult.hasErrors();
	}

	private String redirectOnErrors(final String mode, final VehicleOwnerForm ownerForm, final Errors errors,
			final RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute(VEHICLE_OWNER_FORM_REDIRECT_ERRORS, errors);
		redirectAttributes.addFlashAttribute(VEHICLE_OWNER_FORM, ownerForm);
		if (CREATE_MODE.equals(mode)) {
			return REDIRECT_VEHICLE_OWNER_DETAILS_CREATE;
		} else {
			redirectAttributes.addAttribute(RO_ID_CARD, ownerForm.getRoIdCard());
			return REDIRECT_VEHICLE_OWNER_DETAILS_UPDATE;
		}
	}

}
