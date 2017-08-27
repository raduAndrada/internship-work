package ro.axonsoft.internship172.model.vehicleOwner;

import java.time.Instant;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
@JsonSerialize(as = ImtVehicleOwnerBasic.class)
@JsonDeserialize(builder = ImtVehicleOwnerBasic.Builder.class)
public interface VehicleOwnerBasic {
	/**
	 *
	 * @return cardul de identitate inscris in tabela
	 */
	@Nullable
	String getRoIdCard();

	/**
	 *
	 * @return data de referinta
	 */
	@Nullable
	Instant getIssueDate();

	/**
	 *
	 * @return numarul de inmatriculare al cetateanului
	 */
	@Nullable
	String getRegPlate();

	/**
	 *
	 * @return linie suplimentara
	 */
	@Nullable
	String getComentariu();

}