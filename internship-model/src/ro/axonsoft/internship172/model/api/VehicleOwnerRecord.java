package ro.axonsoft.internship172.model.api;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Interfata pentru proprietarul unei masinii
 *
 * @author intern
 *
 */
@Value.Immutable
@Serial.Version(1)
@JsonSerialize(as = ImtVehicleOwnerRecord.class)
@JsonDeserialize(builder = ImtVehicleOwnerRecord.Builder.class)
public interface VehicleOwnerRecord {

	/**
	 * Metoda pentru determinarea cartii de identitate
	 *
	 * @return proprietatiile pentru cardul de identitate
	 */
	@Nullable
	public RoIdCardProperties getIdCard();

	/**
	 * Metoda pentru aflarea proprietatiilor unui numar de inmatriculare
	 *
	 * @return proprietatiile pentru numarul de inmatriculare
	 */
	@Nullable
	public RoRegPlateProperties getRegPlate();

	/**
	 * Data la care expira
	 *
	 * @return data expirarii
	 */
	@Nullable
	public Date getIdCardIssueDate();
}
