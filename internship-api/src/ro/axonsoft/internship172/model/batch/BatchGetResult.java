package ro.axonsoft.internship172.model.batch;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Batch;
import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonSerialize(as = ImtBatchGetResult.class)
@JsonDeserialize(builder = ImtBatchGetResult.Builder.class)
public interface BatchGetResult {
	Integer getCount();

	@Nullable
	Integer getPageCount();

	@Nullable
	Pagination getPagination();

	List<Batch> getList();
}
