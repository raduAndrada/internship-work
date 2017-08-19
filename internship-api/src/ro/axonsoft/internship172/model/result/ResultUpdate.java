package ro.axonsoft.internship172.model.result;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImtResultUpdate.Builder.class)
public interface ResultUpdate {
	ResultUpdateProperties getProperties();

	Long getId();
}
