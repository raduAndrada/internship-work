package ro.axonsoft.internship172.model.api;

import java.util.Map;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Interfață specificând proprietățile rezultatului procesării proprietarilor de
 * autovehicule.
 */
@Value.Immutable
@Serial.Version(1)
public interface VehicleOwnersMetrics {

    /**
     * Raportul dintre numerele de înmatriculare fără soț și cele cu soț*
     * înmuțit cu 100 și rotunjit half-up fără zecimale.
     */
    Integer getOddToEvenRatio();

    /**
     * Numărul de mașini străine deținute de cetățeni românii pe fiecare județ
     * în parte.
     */
    Map<String, Integer> getUnregCarsCountByJud();

    /**
     * Numărul de autovehicule deținute de persoane având domiciliul într-un
     * județ și autovehiculul înmatriculat în alt județ și au trecut mai multe
     * de 30 de zile de la data emiterii cărții de indentitate.
     */
    Integer getPassedRegChangeDueDate();

}
