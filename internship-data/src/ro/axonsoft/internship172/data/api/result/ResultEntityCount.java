package ro.axonsoft.internship172.data.api.result;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface ResultEntityCount {
	@Value.Parameter
	ResultEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
