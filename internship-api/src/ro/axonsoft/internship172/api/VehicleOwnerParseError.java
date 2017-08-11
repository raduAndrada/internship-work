package ro.axonsoft.internship172.api;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Eroare la o linie din datele de intrare proprietari autovehicule.
 *
 */
@Value.Immutable
@Serial.Version(1)
public interface VehicleOwnerParseError {

    /**
     * Numărul liniei la care a apărut eroarea.
     */
    @Value.Parameter
    Integer getLine();

    /**
     * 0 - invalid line, 1 - invalid CI, 2 - invalid date
     */
    @Value.Parameter
    Integer getType();
}