package ro.axonsoft.internship172.data.api.batch;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.batch.BatchSortCriterion;

@Value.Immutable
public interface BatchEntityGet {
	List<BatchSortCriterion> getSort();

	@Nullable
	Pagination getPagination();

	@Nullable
	BatchEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
