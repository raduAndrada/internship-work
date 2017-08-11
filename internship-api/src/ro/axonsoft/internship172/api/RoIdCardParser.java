package ro.axonsoft.internship172.api;
/**
 * Interfata de parsarea a unei carti de identitate
 * @author intern
 *
 */
public interface RoIdCardParser {

    /**
     * Parsarea unui string cu datele de pe cartea de identitate
     * @param idCard cartea de identitate
     * @return proprietatiile cartii
     * @throws InvalidRoIdCardException exceptia care poate aparea pentru o carte invalida
     */
public RoIdCardProperties parseIdCard(String idCard) throws InvalidRoIdCardException;
}
