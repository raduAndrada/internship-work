package ro.axonsoft.internship172.data.api.vehicleOwner;

import java.util.Set;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

@Value.Immutable
@Serial.Version(1)
public interface VehicleOwnerEntityCriteria {
	Set<String> getRoIdCardIncl();

	Set<Long> getIdIncl();

	Set<Long> getIdBatchSelect();
}
