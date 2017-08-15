package ro.axonsoft.internship172.model.result;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
@JsonSerialize(as = ImtResultBasic.class)
@JsonDeserialize(builder = ImtResultBasic.Builder.class)
public interface ResultBasic {

	@Nullable
	Integer getOddToEvenRatio();

	@Nullable
	Integer getPassedRegChangeDueDate();

	@Nullable
	java.sql.Timestamp getResultProcessTime();
}
