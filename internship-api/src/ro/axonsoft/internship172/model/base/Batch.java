package ro.axonsoft.internship172.model.base;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = ImtBatch.class)
@JsonDeserialize(as = ImtBatch.class)
public interface Batch {

	@Nullable
	@Value.Parameter
	Long getBatchId();

}
