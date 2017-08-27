package ro.axonsoft.internship172.data.api.vehicleOwner;

import java.time.Instant;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface VehicleOwnerEntityUpdate {

	@Nullable
	String getRoIdCard();

	@Nullable
	String getRegPlate();

	@Nullable
	Instant getIssueDate();

	@Nullable
	String getComentariu();

	@Nullable
	String getBatchId();

	@Nullable
	VehicleOwnerEntityCriteria getCriteria();
}
