package ro.axonsoft.internship172.model.vehicleOwner;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Batch;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerCreate.class)
@JsonDeserialize(as = ImtVehicleOwnerCreate.class)
public interface VehicleOwnerCreate {
	VehicleOwnerBasic getBasic();

	@Nullable
	Batch getBatch();
}
