package ro.axonsoft.internship172.model.result;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
@JsonSerialize(as = ImtResultErrorBasic.class)
@JsonDeserialize(builder = ImtResultErrorBasic.Builder.class)
public interface ResultErrorBasic {
	@Nullable
	Integer getType();

	@Nullable
	Long getVehicleOwnerId();

}
