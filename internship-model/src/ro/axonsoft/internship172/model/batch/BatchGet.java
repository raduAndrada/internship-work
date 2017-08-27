package ro.axonsoft.internship172.model.batch;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonDeserialize(builder = ImtBatchGet.Builder.class)
public interface BatchGet {
	@Nullable
	Pagination getPagination();

	List<BatchSortCriterion> getSort();

	@Nullable
	Long getBatchId();

	@Nullable
	String getSearch();
}
