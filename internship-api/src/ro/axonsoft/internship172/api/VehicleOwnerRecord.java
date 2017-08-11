package ro.axonsoft.internship172.api;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Interfata pentru proprietarul unei masinii
 * @author intern
 *
 */
@Value.Immutable
@Serial.Version(1)
public interface VehicleOwnerRecord {

    /**
     * Metoda pentru determinarea cartii de identitate
     * @return proprietatiile pentru cardul de identitate
     */
    public RoIdCardProperties getIdCard();

    /**
     * Metoda pentru aflarea proprietatiilor unui numar de inmatriculare
     * @return proprietatiile pentru numarul de inmatriculare
     */
    @Nullable
    public RoRegPlateProperties getRegPlate();

    /**
     * Data la care expira
     * @return data expirarii
     */
    public Date getIdCardIssueDate();
}
