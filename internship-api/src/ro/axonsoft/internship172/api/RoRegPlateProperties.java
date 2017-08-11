package ro.axonsoft.internship172.api;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

/**
 * Interfata de parsare a proprietatiilor unui numar de inmatriculare
 * @author intern
 *
 */
@Value.Immutable
@Serial.Version(1)
public interface RoRegPlateProperties {

/**
 * Determinarea judetului
 * @return judetul in care este inmatriculat automobilul
 */
public Judet getJudet();

/**
 * Aflarea cifrelor incluse in numar
 * @return cifrele inscrise pe numarul de inmatriculare al masinii
 */
public Short getDigits();

/**
 * Litere corespunzatoare numarului de inmatriculare
 * @return litere din numarul masinii
 */
public String getLetters();
}
