package ro.axonsoft.internship172.model.vehicleOwner;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerUpdateProperties.class)
@JsonDeserialize(as = ImtVehicleOwnerUpdateProperties.class)
public interface VehicleOwnerUpdateProperties {

	String getRoIdCard();

	String getRegPlate();

	String getComentariu();

	Instant getIssueDate();
}
