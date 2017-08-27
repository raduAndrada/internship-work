package ro.axonsoft.internship172.model.vehicleOwner;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImtVehicleOwnerUpdate.Builder.class)
public interface VehicleOwnerUpdate {

	VehicleOwnerUpdateProperties getProperties();

	String getRoIdCard();

}
