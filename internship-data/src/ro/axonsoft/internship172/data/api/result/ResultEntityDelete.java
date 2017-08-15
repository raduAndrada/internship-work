package ro.axonsoft.internship172.data.api.result;

import org.immutables.value.Value;

@Value.Immutable
public interface ResultEntityDelete {
	ResultEntityCriteria getCriteria();
}
