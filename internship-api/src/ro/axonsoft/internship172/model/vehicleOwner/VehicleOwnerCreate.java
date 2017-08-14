package ro.axonsoft.internship172.model.vehicleOwner;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Batch;

@Value.Immutable
@JsonDeserialize(builder = ImtVehicleOwnerCreate.Builder.class)
public interface VehicleOwnerCreate {
	VehicleOwnerBasic getBasic();

	@Nullable
	Batch getBatch();
}
