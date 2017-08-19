package ro.axonfost.internship172.business.impl.result;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.util.Lists;

import com.google.common.collect.ImmutableSet;

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonsoft.internship172.data.api.result.ImtChildTableCriteria;
import ro.axonsoft.internship172.data.api.result.ImtResultEntityCount;
import ro.axonsoft.internship172.data.api.result.ImtResultEntityCriteria;
import ro.axonsoft.internship172.data.api.result.ImtResultEntityDelete;
import ro.axonsoft.internship172.data.api.result.ImtResultEntityGet;
import ro.axonsoft.internship172.data.api.result.ImtResultEntityUpdate;
import ro.axonsoft.internship172.data.api.result.ImtResultErrorEntityDelete;
import ro.axonsoft.internship172.data.api.result.ImtResultUnregCarsCountByJudEntityDelete;
import ro.axonsoft.internship172.data.api.result.MdfResultEntity;
import ro.axonsoft.internship172.data.api.result.ResultEntity;
import ro.axonsoft.internship172.data.api.result.ResultEntityCount;
import ro.axonsoft.internship172.data.api.result.ResultEntityDelete;
import ro.axonsoft.internship172.data.api.result.ResultEntityGet;
import ro.axonsoft.internship172.data.api.result.ResultEntityUpdate;
import ro.axonsoft.internship172.data.api.result.ResultMetricsDao;
import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.result.ImtResultBasic;
import ro.axonsoft.internship172.model.result.ImtResultErrorRecord;
import ro.axonsoft.internship172.model.result.ImtResultMetricsCreateResult;
import ro.axonsoft.internship172.model.result.ImtResultMetricsDeleteResult;
import ro.axonsoft.internship172.model.result.ImtResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ImtResultMetricsUpdateResult;
import ro.axonsoft.internship172.model.result.ImtResultRecord;
import ro.axonsoft.internship172.model.result.ImtResultUnregCarsCountByJudRecord;
import ro.axonsoft.internship172.model.result.ImtResultUpdateProperties;
import ro.axonsoft.internship172.model.result.MdfResultBasic;
import ro.axonsoft.internship172.model.result.MdfResultRecord;
import ro.axonsoft.internship172.model.result.ResultBasic;
import ro.axonsoft.internship172.model.result.ResultCreate;
import ro.axonsoft.internship172.model.result.ResultErrorBasic;
import ro.axonsoft.internship172.model.result.ResultErrorRecord;
import ro.axonsoft.internship172.model.result.ResultGet;
import ro.axonsoft.internship172.model.result.ResultMetricsCreateResult;
import ro.axonsoft.internship172.model.result.ResultMetricsDeleteResult;
import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultMetricsUpdateResult;
import ro.axonsoft.internship172.model.result.ResultRecord;
import ro.axonsoft.internship172.model.result.ResultUnregCarsCountByJudBasic;
import ro.axonsoft.internship172.model.result.ResultUnregCarsCountByJudRecord;
import ro.axonsoft.internship172.model.result.ResultUpdate;

public class ResultBusinessImpl implements ResultBusiness {

	private ResultMetricsDao resDao;

	@Inject
	public void setResultMetricsDao(ResultMetricsDao resDao) {
		this.resDao = resDao;
	}

	@Override
	public ResultMetricsCreateResult createResult(ResultCreate resCreate) throws BusinessException {
		final ResultEntity resEntity = buildResultEntityForCreate(resCreate);
		resDao.addResult(resEntity);
		final List<ResultErrorRecord> errors = buildResultErrorsForCreate(resEntity);
		if (errors.size() != 0) {
			resDao.addErrors(errors);
		}
		final List<ResultUnregCarsCountByJudRecord> unregCars = buildResultUnregCarsCountByJudRecordForCreate(
				resEntity);
		if (unregCars.size() != 0) {
			resDao.addUnregCars(unregCars);
		}
		return ImtResultMetricsCreateResult.builder().basic(ImtResultBasic.copyOf(resEntity.getRecord().getBasic()))
				.build();
	}

	private List<ResultUnregCarsCountByJudRecord> buildResultUnregCarsCountByJudRecordForCreate(
			ResultEntity resEntity) {

		final List<ResultUnregCarsCountByJudRecord> unregCars = Lists.newArrayList();
		resEntity.getUnregCars().forEach(e -> {
			final ResultUnregCarsCountByJudBasic basic = e.getBasic();
			unregCars.add(ImtResultUnregCarsCountByJudRecord.builder().resultMetricsId(resEntity.getResultMetricsId())
					.basic(basic).build());
		});
		return unregCars;
	}

	private List<ResultErrorRecord> buildResultErrorsForCreate(ResultEntity resEntity) {
		final List<ResultErrorRecord> errors = Lists.newArrayList();
		resEntity.getErrors().forEach(e -> {
			final ResultErrorBasic basicError = e.getBasic();
			errors.add(ImtResultErrorRecord.builder().resultMetricsId(resEntity.getResultMetricsId()).basic(basicError)
					.build());
		});
		return errors;
	}

	private ResultEntity buildResultEntityForCreate(ResultCreate resCreate) {
		final ResultEntity resEntity = MdfResultEntity.create().setErrors(resCreate.getErrors())
				.setUnregCars(resCreate.getUnregCars())
				.setRecord(MdfResultRecord.create()
						.setBasic(MdfResultBasic.create().from(resCreate.getBasic())
								.setOddToEvenRatio(resCreate.getBasic().getOddToEvenRatio())
								.setPassedRegChangeDueDate(resCreate.getBasic().getPassedRegChangeDueDate())
								.setResultProcessTime(resCreate.getBasic().getResultProcessTime()))
						.setBatch(resCreate.getBatch()));
		return resEntity;
	}

	@Override
	public ResultMetricsUpdateResult updateResult(ResultUpdate resUpdate) throws BusinessException {
		final ResultEntityUpdate resEntityUpdate = buildResultEntityForUpdate(resUpdate);

		if (!resultChanged(resEntityUpdate)) {

			throw new BusinessException("No field updated", null);
		}
		resDao.updateResult(resEntityUpdate);

		return ImtResultMetricsUpdateResult.builder()
				.properties(ImtResultUpdateProperties.builder().oddToEvenRatio(resEntityUpdate.getOddToEvenRatio())
						.passedChangeDueDate(resEntityUpdate.getPassedRegChangeDueDate())
						.resultProcessTime(resEntityUpdate.getResultProcessTime()).build())
				.build();
	}

	private boolean resultChanged(ResultEntityUpdate resEntityUpdate) {
		final List<ResultEntity> results = resDao
				.getResult(ImtResultEntityGet.builder()
						.criteria(ImtResultEntityCriteria.builder()
								.addIdIncl(resEntityUpdate.getCriteria().getIdIncl().iterator().next()).build())
						.build());
		if (!results.isEmpty()) {
			final ResultBasic res = results.get(0).getRecord().getBasic();

			if (res.getOddToEvenRatio().equals(resEntityUpdate.getOddToEvenRatio())
					&& res.getPassedRegChangeDueDate().equals(resEntityUpdate.getPassedRegChangeDueDate())
					&& sameProperty(res.getResultProcessTime().toString(),
							resEntityUpdate.getResultProcessTime().toString())) {
				return false;
			}
			return true;
		}

		return false;

	}

	private ResultEntityUpdate buildResultEntityForUpdate(ResultUpdate resUpdate) {
		final ResultEntityUpdate resEntityUpdate = ImtResultEntityUpdate.builder()
				.criteria(ImtResultEntityCriteria.builder().addIdIncl(resUpdate.getId()).build())
				.oddToEvenRatio(resUpdate.getProperties().getOddToEvenRatio() != null
						? resUpdate.getProperties().getOddToEvenRatio()
						: null)
				.passedRegChangeDueDate(resUpdate.getProperties().getPassedChangeDueDate() != null
						? resUpdate.getProperties().getPassedChangeDueDate()
						: null)
				.resultProcessTime(resUpdate.getProperties().getResultProcessTime() != null
						? resUpdate.getProperties().getResultProcessTime()
						: null)
				.build();

		return resEntityUpdate;
	}

	@Override
	public ResultMetricsDeleteResult deleteResult(Long id) throws BusinessException {
		final ResultEntityDelete resEntityDelete = buildResultEntityForDelete(id);

		final ResultEntity res = checkResultForDelete(id);

		resDao.deleteResultError(ImtResultErrorEntityDelete.builder()
				.criteria(ImtChildTableCriteria.builder().addIdResultSelect(id).build()).build());
		resDao.deleteResultUnregCarsCountByJud(ImtResultUnregCarsCountByJudEntityDelete.builder()
				.criteria(ImtChildTableCriteria.builder().addIdResultSelect(id).build())

				.build());
		resDao.deleteResult(resEntityDelete);

		return ImtResultMetricsDeleteResult.builder()
				.basic(ImtResultBasic.builder().oddToEvenRatio(res.getRecord().getBasic().getOddToEvenRatio())

						.passedRegChangeDueDate(res.getRecord().getBasic().getPassedRegChangeDueDate())
						.resultProcessTime(res.getRecord().getBasic().getResultProcessTime()).build())
				.build();
	}

	private ResultEntity checkResultForDelete(Long id) {

		final List<ResultEntity> results = resDao.getResult(
				ImtResultEntityGet.builder().criteria(ImtResultEntityCriteria.builder().addIdIncl(id).build()).build());

		if (results.isEmpty()) {
			throw new BusinessException("VehicleOwner not found", null, null);
		}
		return results.get(0);
	}

	private ResultEntityDelete buildResultEntityForDelete(Long id) {
		final ResultEntityDelete resEntityDelete = ImtResultEntityDelete.builder()
				.criteria(ImtResultEntityCriteria.builder().addIdIncl(id).build()).build();

		return resEntityDelete;
	}

	@Override
	public ResultMetricsGetResult getResults(ResultGet resGet) {
		final ResultEntityGet resEntityGet = buildResultEntityGet(resGet);
		final ResultEntityCount resEntityCount = buildResultEntityCount(resGet);
		final int count = resDao.countResult(resEntityCount);
		final List<ResultEntity> results = resDao.getResult(resEntityGet);
		return ImtResultMetricsGetResult.builder()
				.list(results.stream().map(ResultEntity::getRecord).map(ImtResultRecord::copyOf)
						.map(ResultRecord.class::cast)::iterator)
				.count(count).pagination(resGet.getPagination()).pageCount(getPageCount(count, resGet.getPagination()))
				.build();
	}

	private ResultEntityCount buildResultEntityCount(ResultGet resGet) {
		return ImtResultEntityCount.builder().criteria(ImtResultEntityCriteria.builder().idIncl(
				resGet.getResultMetricsId() != null ? ImmutableSet.of(resGet.getResultMetricsId()) : ImmutableSet.of())
				.build()).search(resGet.getSearch()).build();
	}

	private ResultEntityGet buildResultEntityGet(ResultGet resGet) {
		return ImtResultEntityGet.builder().pagination(resGet.getPagination()).search(resGet.getSearch())
				.sort(resGet.getSort())
				.criteria(ImtResultEntityCriteria.builder()
						.idIncl(resGet.getResultMetricsId() != null ? ImmutableSet.of(resGet.getResultMetricsId())
								: ImmutableSet.of())
						.idBatchSelect(
								resGet.getBatchId() != null ? ImmutableSet.of(resGet.getBatchId()) : ImmutableSet.of())
						.build())
				.build();
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

}
