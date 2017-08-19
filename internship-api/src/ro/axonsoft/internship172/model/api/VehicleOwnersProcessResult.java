package ro.axonsoft.internship172.model.api;

import java.util.Set;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Interfață specificând proprietățile rezultatului procesării proprietarilor de
 * autovehicule.
 */
@Value.Immutable
@Serial.Version(1)
public interface VehicleOwnersProcessResult {

    VehicleOwnersMetrics getMetrics();

    /**
     * Erorile de procesare.
     */
    Set<VehicleOwnerParseError> getErrors();
}
