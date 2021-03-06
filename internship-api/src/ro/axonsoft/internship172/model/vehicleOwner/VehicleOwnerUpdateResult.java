package ro.axonsoft.internship172.model.vehicleOwner;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerUpdateResult.class)
@JsonDeserialize(builder = ImtVehicleOwnerUpdateResult.Builder.class)
public interface VehicleOwnerUpdateResult {

	VehicleOwnerUpdateProperties getProperties();

}
