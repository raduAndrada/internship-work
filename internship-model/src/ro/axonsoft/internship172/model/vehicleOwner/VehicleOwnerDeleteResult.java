package ro.axonsoft.internship172.model.vehicleOwner;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerDeleteResult.class)
@JsonDeserialize(builder = ImtVehicleOwnerDeleteResult.Builder.class)
public interface VehicleOwnerDeleteResult {

	VehicleOwnerBasic getBasic();
}
