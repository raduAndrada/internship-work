package ro.axonsoft.internship172.web.model;

import org.immutables.value.Value;

@Value.Immutable
public interface SuccessMessage {

	String getKey();

	Object[] getVars();
}
