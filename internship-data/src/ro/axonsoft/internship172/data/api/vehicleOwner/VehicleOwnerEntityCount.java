package ro.axonsoft.internship172.data.api.vehicleOwner;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface VehicleOwnerEntityCount {
	@Value.Parameter
	VehicleOwnerEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
