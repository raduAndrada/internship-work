package ro.axonsoft.internship172.data.domain;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import ro.axonsoft.internship172.api.Judet;

/**
 * Clasa model pentru tabela de UNREG_CARS_COUNT_BY_JUD
 *
 * @author Andrada
 *
 */

@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
public interface ResultUnregCarsCountByJud {

    /**
     * Judetul de care apartine masina neinregistrat
     *
     * @return enum cu judetel respectiv
     */
    @Parameter
    @Nullable
    Judet getJudet();

    /**
     *
     * @return numarul de masini neinregistrate din judetul judet
     */
    @Parameter
    @Nullable
    Integer getUnregCarsCount();


    /**
     *
     * @return identificatorul unic al tabelei
     */
    @Nullable
    @Parameter
    Long getUnregCarsId();

    /**
     *
     * @return metricea rezultat de care apartine
     */
    @Nullable
    @Parameter
    Long getResultMetricsId();

}
