package ro.axonsoft.internship172.api;

/**
 * Interfata de implementat pentru procesarea asupra unei baze de date
 * @author Andrada
 *
 */
public interface DbVehicleOwnersProcessor {

    void process(Long batchId);

}
