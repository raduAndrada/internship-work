package ro.axonsoft.internship172.api;

import java.util.Iterator;

/**
 *
 * Procesator de date proprietati de autovehicule.
 *
 */
public interface VehicleOwnersProcessor {

    VehicleOwnersMetrics process(Iterator<VehicleOwnerRecord> records);
}
