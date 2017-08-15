package ro.axonsoft.internship172.data.api.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.result.ResultSortCriterion;

@Value.Immutable
public interface ResultEntityGet {
	List<ResultSortCriterion> getSort();

	@Nullable
	Pagination getPagination();

	@Nullable
	ResultEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
