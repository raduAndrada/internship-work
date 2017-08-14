package ro.axonsoft.internship172.data.api.vehicleOwner;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerBasicRecord;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
public interface VehicleOwnerEntity {

	@Nullable
	VehicleOwnerBasicRecord getRecord();

	@Nullable
	Long getVehicleOwnerId();
}
