package ro.axonsoft.internship172.model.result;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = ImtResultErrorRecord.class)
@JsonDeserialize(builder = ImtResultErrorRecord.Builder.class)
public interface ResultErrorRecord {
	@Nullable
	ResultErrorBasic getBasic();

	@Nullable
	Long getResultMetricsId();

	@Nullable
	Long getResultErrorId();
}
