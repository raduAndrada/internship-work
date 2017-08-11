package ro.axonsoft.internship172.data.domain;

import java.sql.Date;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Clasa model pentru o inregistrare din tabela cu datele ce trebuie procesate
 * VEHICLE_OWNER
 *
 * @author Andrada
 *
 */
@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
public interface VehicleOwner {
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
    Date getIssueDate();

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


    /**
     *
     * @return identificatorul unic al inregistrarii
     */
    @Nullable
    Long getVehicleOwnerId();

    /**
     * batch-ul de procesare
     * @return cheia straina spre tabela de batch
     */
    @Nullable
    Long getBatchId();

}
