package ro.axonsoft.internship172.api;
/**
 * Parsarea unui numar de inmatriculare
 * @author intern
 *
 */
public interface RoRegPlateParser {


    /**
     * Metoda pentru parsarea numarului de inmatriculare
     * @param registrationPlate String cu numarul care trebuie parsat
     * @return proprietatiile numarului masinii
     */

    public RoRegPlateProperties parseRegistrationPlate(String registrationPlate) throws InvalidRoRegPlateException;

}
