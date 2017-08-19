package ro.axonsoft.internship172.model.result;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.vehicleOwner.ImtVehicleOwnerUpdateProperties;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerUpdateProperties.class)
@JsonDeserialize(as = ImtVehicleOwnerUpdateProperties.class)
public interface ResultUpdateProperties {
	Integer getOddToEvenRatio();

	Integer getPassedChangeDueDate();

	Instant getResultProcessTime();
}
