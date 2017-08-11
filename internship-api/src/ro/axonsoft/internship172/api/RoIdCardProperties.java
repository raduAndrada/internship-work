package ro.axonsoft.internship172.api;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Interfata folosita pentru a determina proprietatiile cartii de identitate
 * @author intern
 *
 */
@Value.Immutable
@Serial.Version(1)
public interface RoIdCardProperties {

/**
* aflarea judetului
* @return judetul pentru cartea de identitate
*/
public Judet getJudet();

/**
 * determinarea seriei de buletin
 * @return seria cartii de identitate
 */
public String getSeries();

/**
 * aflarea numarului
 * @return numarul pentru buletin
 */
public Integer getNumber();
}
