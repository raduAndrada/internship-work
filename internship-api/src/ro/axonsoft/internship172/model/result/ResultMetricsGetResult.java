package ro.axonsoft.internship172.model.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonSerialize(as = ImtResultMetricsGetResult.class)
@JsonDeserialize(builder = ImtResultMetricsGetResult.Builder.class)
public interface ResultMetricsGetResult {
	Integer getCount();

	@Nullable
	Integer getPageCount();

	@Nullable
	Pagination getPagination();

	List<ResultRecord> getList();

}
