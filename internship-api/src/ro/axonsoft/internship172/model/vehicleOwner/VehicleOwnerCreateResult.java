package ro.axonsoft.internship172.model.vehicleOwner;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.ResultBatch;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerCreateResult.class)
@JsonDeserialize(builder = ImtVehicleOwnerCreateResult.Builder.class)
public interface VehicleOwnerCreateResult {
	VehicleOwnerBasic getBasic();

	@Nullable
	ResultBatch getBatch();
}
