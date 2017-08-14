package ro.axonsoft.internship172.model.vehicleOwner;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Batch;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = ImtVehicleOwnerBasicRecord.class)
@JsonDeserialize(builder = ImtVehicleOwnerBasicRecord.Builder.class)
public interface VehicleOwnerBasicRecord {

	@Nullable
	VehicleOwnerBasic getBasic();

	@Nullable
	Batch getBatch();

}
