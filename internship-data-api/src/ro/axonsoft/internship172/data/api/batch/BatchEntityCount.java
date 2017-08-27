package ro.axonsoft.internship172.data.api.batch;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface BatchEntityCount {
	@Value.Parameter
	BatchEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
