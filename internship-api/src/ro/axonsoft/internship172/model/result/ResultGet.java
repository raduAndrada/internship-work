package ro.axonsoft.internship172.model.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonDeserialize(builder = ImtResultGet.Builder.class)
public interface ResultGet {
	@Nullable
	Pagination getPagination();

	List<ResultSortCriterion> getSort();

	@Nullable
	Long getResultMetricsId();

	@Nullable
	Long getBatchId();

	@Nullable
	String getSearch();
}
