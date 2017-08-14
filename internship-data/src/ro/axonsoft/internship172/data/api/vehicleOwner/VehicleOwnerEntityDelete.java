package ro.axonsoft.internship172.data.api.vehicleOwner;

import org.immutables.value.Value;

@Value.Immutable
public interface VehicleOwnerEntityDelete {

	VehicleOwnerEntityCriteria getCriteria();
}
