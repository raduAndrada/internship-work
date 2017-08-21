package ro.axonsoft.internship172.business.impl.vehicleOwner;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;

import ro.axonsoft.internship172.business.api.vehicleOwner.VehicleOwnerBusiness;
import ro.axonsoft.internship172.data.api.batch.BatchEntityCount;
import ro.axonsoft.internship172.data.api.batch.BatchEntityGet;
import ro.axonsoft.internship172.data.api.batch.ImtBatchEntityCount;
import ro.axonsoft.internship172.data.api.batch.ImtBatchEntityCriteria;
import ro.axonsoft.internship172.data.api.batch.ImtBatchEntityGet;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityCount;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityCriteria;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityDelete;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityGet;
import ro.axonsoft.internship172.data.api.vehicleOwner.ImtVehicleOwnerEntityUpdate;
import ro.axonsoft.internship172.data.api.vehicleOwner.MdfVehicleOwnerEntity;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerDao;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntity;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntityCount;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntityDelete;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntityGet;
import ro.axonsoft.internship172.data.api.vehicleOwner.VehicleOwnerEntityUpdate;
import ro.axonsoft.internship172.model.api.RoIdCardParser;
import ro.axonsoft.internship172.model.base.Batch;
import ro.axonsoft.internship172.model.base.ImtBatch;
import ro.axonsoft.internship172.model.base.MdfBatch;
import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.batch.BatchCreate;
import ro.axonsoft.internship172.model.batch.BatchCreateResult;
import ro.axonsoft.internship172.model.batch.BatchGet;
import ro.axonsoft.internship172.model.batch.BatchGetResult;
import ro.axonsoft.internship172.model.batch.BatchSortCriterionType;
import ro.axonsoft.internship172.model.batch.ImtBatchCreate;
import ro.axonsoft.internship172.model.batch.ImtBatchCreateResult;
import ro.axonsoft.internship172.model.batch.ImtBatchGetResult;
import ro.axonsoft.internship172.model.batch.ImtBatchSortCriterion;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.error.ErrorPropertiesBuilder;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerUpdateProperties;
import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerUpdateResult;
import ro.axonsoft.internship172.model.vehicleOwner.MdfVehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.MdfVehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.RoIdCardInvalidErrorSpec;
import ro.axonsoft.internship172.model.vehicleOwner.RoIdCardTakenErrorSpec;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerBasic;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerBasicRecord;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGet;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerUpdate;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerUpdateResult;

public class VehicleOwnerBusinessImpl implements VehicleOwnerBusiness {

	private VehicleOwnerDao vehicleOwnerDao;

	@Inject
	public void setVehicleOwnerDao(VehicleOwnerDao vehicleOwnerDao) {
		this.vehicleOwnerDao = vehicleOwnerDao;
	}

	RoIdCardParser roIdCardParser;

	@Inject
	public void setRoIdCardParser(final RoIdCardParser roIdCardParser) {
		this.roIdCardParser = roIdCardParser;
	}

	@Override
	public VehicleOwnerCreateResult createVehicleOwner(VehicleOwnerCreate vhoCreate) throws BusinessException {
		final VehicleOwnerEntity vhoEntity = buildVehicleOwnerEntityForCreate(vhoCreate);
		if (!checkRoIdCard(vhoEntity)) {
			throw new BusinessException("Id card exists!",
					ErrorPropertiesBuilder.of(RoIdCardTakenErrorSpec.RO_ID_CARD_TAKEN)
							.var(RoIdCardTakenErrorSpec.Var.RO_ID_CARD, vhoEntity.getRecord().getBasic().getRoIdCard())
							.build());
		}

		if (!checkParseRoIdCard(vhoEntity)) {
			throw new BusinessException("Id card is not valid!",
					ErrorPropertiesBuilder.of(RoIdCardInvalidErrorSpec.RO_ID_CARD_INVALID)
							.var(RoIdCardInvalidErrorSpec.Var.RO_ID_CARD_INVALID,
									vhoEntity.getRecord().getBasic().getRoIdCard())
							.build());
		}

		if (!checkBatchExists(vhoEntity)) {
			final Batch batchEntity = buildBatchForCreate(
					ImtBatchCreate.builder().batch(ImtBatch.builder().build()).build());
			vehicleOwnerDao.addBatch(batchEntity);
		}

		vehicleOwnerDao.addVehicleOwner(vhoEntity);
		return ImtVehicleOwnerCreateResult.builder()
				.basic(ImtVehicleOwnerBasic.copyOf(vhoEntity.getRecord().getBasic())).build();
	}

	private boolean checkBatchExists(VehicleOwnerEntity vhoEntity) {

		return vehicleOwnerDao
				.getBatch(ImtBatchEntityGet.builder()
						.addSort(ImtBatchSortCriterion.of(BatchSortCriterionType.BATCH_ID, SortDirection.DESC))
						.criteria(ImtBatchEntityCriteria.builder()
								.addIdBatchSelect(vhoEntity.getRecord().getBatch().getBatchId()).build())
						.build())
				.size() > 0;
	}

	private boolean checkParseRoIdCard(VehicleOwnerEntity vhoEntity) {
		try {
			roIdCardParser.parseIdCard(vhoEntity.getRecord().getBasic().getRoIdCard());
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public BatchCreateResult createBatch(BatchCreate batchCreate) throws BusinessException {
		final Batch batchEntity = buildBatchForCreate(batchCreate);
		if (batchEntity.getBatchId() != null) {
			throw new BusinessException("Batch id must be null", null);
		}
		vehicleOwnerDao.addBatch(batchEntity);
		return ImtBatchCreateResult.builder().batch(ImtBatch.copyOf(batchEntity)).build();
	}

	@Override
	public VehicleOwnerUpdateResult updateVehicleOwner(VehicleOwnerUpdate vhoUpdate) throws BusinessException {

		final VehicleOwnerEntityUpdate vhoEntityUpdate = buildVehicleOwnerEntityForUpdate(vhoUpdate);

		if (!vehicleOwnerChanged(vhoEntityUpdate)) {

			throw new BusinessException("No field updated", null);
		}
		vehicleOwnerDao.updateVehicleOwner(vhoEntityUpdate);

		return ImtVehicleOwnerUpdateResult.builder()
				.properties(ImtVehicleOwnerUpdateProperties.builder().roIdCard(vhoEntityUpdate.getRoIdCard())
						.regPlate(vhoEntityUpdate.getRegPlate()).comentariu(vhoEntityUpdate.getComentariu()).build())
				.build();
	}

	@Override
	public VehicleOwnerDeleteResult deleteVehicleOwner(String roIdCard) throws BusinessException {
		final VehicleOwnerEntityDelete vhoEntityDelete = buildVehicleOwnerEntityForDelete(roIdCard);

		final VehicleOwnerEntity vho = checkVehicleOwnerForDelete(roIdCard);

		vehicleOwnerDao.deleteVehicleOwner(vhoEntityDelete);

		return ImtVehicleOwnerDeleteResult.builder()
				.basic(ImtVehicleOwnerBasic.builder().roIdCard(roIdCard.toUpperCase())
						.regPlate(vho.getRecord().getBasic().getRegPlate())
						.comentariu(vho.getRecord().getBasic().getComentariu())
						.issueDate(vho.getRecord().getBasic().getIssueDate()).build())
				.build();
	}

	@Override
	public VehicleOwnerGetResult getVehicleOwners(final VehicleOwnerGet vhoGet) {
		final VehicleOwnerEntityGet vhoEntityGet = buildVehicleOwnerEntityGet(vhoGet);
		final VehicleOwnerEntityCount vhoEntityCount = buildVehicleOwnerEntityCount(vhoGet);
		final int count = vehicleOwnerDao.countVehicleOwner(vhoEntityCount);
		final List<VehicleOwnerEntity> vhos = vehicleOwnerDao.getVehicleOwner(vhoEntityGet);
		return ImtVehicleOwnerGetResult.builder()
				.list(vhos.stream().map(VehicleOwnerEntity::getRecord).map(ImtVehicleOwnerBasicRecord::copyOf)
						.map(VehicleOwnerBasicRecord.class::cast)::iterator)
				.count(count).pagination(vhoGet.getPagination()).pageCount(getPageCount(count, vhoGet.getPagination()))
				.build();
	}

	@Override
	public BatchGetResult getBatches(BatchGet batchGet) {
		final BatchEntityGet batchEntityGet = buildBatchEntityGet(batchGet);
		final BatchEntityCount batchEntityCount = buildBatchEntityCount(batchGet);
		final int count = vehicleOwnerDao.countBatch(batchEntityCount);
		final List<Batch> batches = vehicleOwnerDao.getBatch(batchEntityGet);
		return ImtBatchGetResult.builder().list(batches).count(count).pagination(batchGet.getPagination())
				.pageCount(getPageCount(count, batchGet.getPagination())).build();
	}

	private BatchEntityCount buildBatchEntityCount(BatchGet batchGet) {
		return ImtBatchEntityCount.builder()
				.criteria(ImtBatchEntityCriteria.builder().idBatchSelect(
						batchGet.getBatchId() != null ? ImmutableSet.of(batchGet.getBatchId()) : ImmutableSet.of())
						.build())
				.search(batchGet.getSearch()).build();
	}

	private BatchEntityGet buildBatchEntityGet(BatchGet batchGet) {
		return ImtBatchEntityGet.builder().pagination(batchGet.getPagination()).search(batchGet.getSearch())
				.sort(batchGet.getSort())
				.criteria(ImtBatchEntityCriteria.builder().idBatchSelect(
						batchGet.getBatchId() != null ? ImmutableSet.of(batchGet.getBatchId()) : ImmutableSet.of())
						.build())
				.build();
	}

	private int getPageCount(final int count, final Pagination pagination) {
		int pageCount;
		if (pagination == null) {
			pageCount = 1;
		} else {
			pageCount = count / pagination.getPageSize();
			if (count % pagination.getPageSize() != 0) {
				pageCount += 1;
			}
		}
		return pageCount;
	}

	private VehicleOwnerEntityCount buildVehicleOwnerEntityCount(final VehicleOwnerGet vhoGet) {
		return ImtVehicleOwnerEntityCount.builder().criteria(ImtVehicleOwnerEntityCriteria.builder()
				.roIdCardIncl(vhoGet.getRoIdCard() != null ? ImmutableSet.of(vhoGet.getRoIdCard().toUpperCase())
						: ImmutableSet.of())
				.idBatchSelect(vhoGet.getBatchId() != null ? ImmutableSet.of(vhoGet.getBatchId()) : ImmutableSet.of())
				.build()).search(vhoGet.getSearch()).build();
	}

	private boolean checkRoIdCard(final VehicleOwnerEntity vhoEntity) {
		return vehicleOwnerDao
				.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
						.criteria(ImtVehicleOwnerEntityCriteria.builder()
								.addRoIdCardIncl(vhoEntity.getRecord().getBasic().getRoIdCard()).build())
						.build())
				.size() < 1;
	}

	private VehicleOwnerEntity buildVehicleOwnerEntityForCreate(final VehicleOwnerCreate vhoCreate) {
		final VehicleOwnerEntity vhoEntity = MdfVehicleOwnerEntity.create()
				.setRecord(MdfVehicleOwnerBasicRecord.create()
						.setBasic(MdfVehicleOwnerBasic.create().from(vhoCreate.getBasic())
								.setComentariu(vhoCreate.getBasic().getComentariu().toLowerCase())
								.setRegPlate(vhoCreate.getBasic().getRegPlate().toUpperCase())
								.setIssueDate(vhoCreate.getBasic().getIssueDate())
								.setRoIdCard(vhoCreate.getBasic().getRoIdCard()))
						.setBatch(vhoCreate.getBatch()));

		return vhoEntity;
	}

	private Batch buildBatchForCreate(final BatchCreate batchCreate) {
		final Batch batchEntity = MdfBatch.create().setBatchId(null);
		return batchEntity;
	}

	private VehicleOwnerEntityUpdate buildVehicleOwnerEntityForUpdate(final VehicleOwnerUpdate vhoUpdate) {
		final VehicleOwnerEntityUpdate vhoEntityUpdate = ImtVehicleOwnerEntityUpdate.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder()
						.roIdCardIncl(ImmutableSet.of(vhoUpdate.getRoIdCard().toUpperCase())).build())
				.roIdCard(vhoUpdate.getProperties().getRoIdCard() != null ? vhoUpdate.getProperties().getRoIdCard()
						: null)
				.comentariu(vhoUpdate.getProperties().getComentariu() != null
						? vhoUpdate.getProperties().getComentariu().toLowerCase()
						: null)
				.regPlate(vhoUpdate.getProperties().getRegPlate() != null
						? vhoUpdate.getProperties().getRegPlate().toLowerCase()
						: null)
				.issueDate(vhoUpdate.getProperties().getIssueDate() != null ? vhoUpdate.getProperties().getIssueDate()
						: null)
				.build();

		return vhoEntityUpdate;
	}

	private VehicleOwnerEntity checkVehicleOwnerForDelete(final String roIdCard) throws BusinessException {

		final List<VehicleOwnerEntity> vhos = vehicleOwnerDao.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().addRoIdCardIncl(roIdCard.toUpperCase()).build())
				.build());

		if (vhos.isEmpty()) {
			throw new BusinessException("VehicleOwner not found", null, null);
		}
		return vhos.get(0);
	}

	private VehicleOwnerEntityDelete buildVehicleOwnerEntityForDelete(final String roIdCard) {
		final VehicleOwnerEntityDelete vhoEntityDelete = ImtVehicleOwnerEntityDelete.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder().roIdCardIncl(ImmutableSet.of(roIdCard)).build())
				.build();

		return vhoEntityDelete;
	}

	private VehicleOwnerEntityGet buildVehicleOwnerEntityGet(final VehicleOwnerGet vhoGet) {
		return ImtVehicleOwnerEntityGet.builder().pagination(vhoGet.getPagination()).search(vhoGet.getSearch())
				.sort(vhoGet.getSort())
				.criteria(ImtVehicleOwnerEntityCriteria.builder()
						.roIdCardIncl(vhoGet.getRoIdCard() != null ? ImmutableSet.of(vhoGet.getRoIdCard().toUpperCase())
								: ImmutableSet.of())
						.idIncl(vhoGet.getVehicleOwnerId() != null ? ImmutableSet.of(vhoGet.getVehicleOwnerId())
								: ImmutableSet.of())
						.idBatchSelect(
								vhoGet.getBatchId() != null ? ImmutableSet.of(vhoGet.getBatchId()) : ImmutableSet.of())
						.build())
				.build();
	}

	private boolean vehicleOwnerChanged(final VehicleOwnerEntityUpdate vhoEntityUpdate) {
		final List<VehicleOwnerEntity> vhos = vehicleOwnerDao.getVehicleOwner(ImtVehicleOwnerEntityGet.builder()
				.criteria(ImtVehicleOwnerEntityCriteria.builder()
						.addRoIdCardIncl(vhoEntityUpdate.getCriteria().getRoIdCardIncl().iterator().next()).build())
				.build());
		if (!vhos.isEmpty()) {
			final VehicleOwnerBasic vho = vhos.get(0).getRecord().getBasic();

			if (sameProperty(vho.getRoIdCard(), vhoEntityUpdate.getRoIdCard())
					&& sameProperty(vho.getComentariu(), vhoEntityUpdate.getComentariu())
					&& sameProperty(vho.getRegPlate(), vhoEntityUpdate.getRegPlate())) {
				return false;
			}
			return true;
		}

		return false;
	}

	private boolean sameProperty(final String newProp, final String dbProp) {
		if (newProp == null) {
			return true;
		}
		if (newProp.equals(dbProp)) {
			return true;
		}
		return false;
	}

}
