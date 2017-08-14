package ro.axonsoft.internship172.model.base;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtPagination.class)
@JsonDeserialize(as = ImtPagination.class)
public interface Pagination {
	@Value.Parameter
	Integer getPage();

	@Value.Parameter
	Integer getPageSize();

	@Value.Default
	default Long getOffset() {
		return getPageSize().longValue() * (getPage() - 1);
	}
}
