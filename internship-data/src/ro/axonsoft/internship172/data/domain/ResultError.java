package ro.axonsoft.internship172.data.domain;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Clasa model pentru extragerea unui obiect de tip RESULT_ERROR din baza de
 * date
 *
 * @author Andrada
 *
 */

@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
public interface ResultError {

	/**
	 * Tipul erori
	 *
	 * @return 0,1,2 in functie de datele din baza de date
	 */

	@Value.Parameter
	@Nullable
	Integer getType();

	/**
	 * Id-ul unic din tabela
	 *
	 * @return cheia primara
	 */

	@Value.Parameter
	@Nullable
	Long getResultErrorId();

	/**
	 * Id-ul cetateanului care are datele inregistrate in tabela cu erori
	 *
	 * @return identificator unic
	 */
	@Nullable
	@Value.Parameter
	Long getVehicleOwnerId();

	/**
	 * Id-ul rezultatului de care tine
	 */
	@Nullable
	@Value.Parameter
	Long getResultMetricsId();

}
