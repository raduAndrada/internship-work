package ro.axonsoft.internship172.data.mappers;

import java.util.List;

import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.model.base.Batch;

/**
 * mapper pentru tabela din baza de date
 *
 * @author intern
 *
 */

public interface VehicleOwnersDao {

	/**
	 * Selecteaza un vehicleOwner din baza de date
	 *
	 * @param id
	 *            id-ul inregistrarii
	 * @return un obiect de tip VehicleOwner cu toate campurile
	 */
	public VehicleOwner selectVehicleOwnerById(Long id);

	/**
	 * MEtoda care selecteaza toti VehicleOwnerii din baza de date
	 *
	 * @return o lista cu VehicleOwnerii
	 */
	public List<VehicleOwner> getVehicleOwnersPage(PageCriteria pageCriteria);

	/**
	 * Selectie pagina de batch
	 *
	 * @param pageCriteria
	 *            criteriu de selectie
	 * @return lista cu batch-uri
	 */
	public List<Batch> getBatchPage(PageCriteria pageCriteria);

	/**
	 * Inserarea unei noi inregistrari
	 *
	 * @param vehicleOwner
	 *            obiect cu datele pentru baza de date
	 */
	public void insertVehicleOwner(VehicleOwner vehicleOwner);

	/**
	 * Returneaza nuamrul de inregistrari din baza de date pentru VehicleOwner
	 *
	 * @param criteria
	 * @return numarul de inregistrari din tabela
	 */

	public Integer countVehicleOwner(VehicleOwnerCriteria criteria);

	/**
	 * Numararea inregistrarilor din tabela de vehicle owner
	 *
	 * @param id
	 *            batch-ul dupa care se face selectia
	 * @return numarul de inregistrari cu batch-ul acesta
	 */
	public Integer countVehicleOwnersByBatchId(Long id);

	/**
	 * Metoda de inserare in tabela de batch
	 */
	public void insertBatch(Batch batch);

	/**
	 * Selecteaza ultimul id de batch
	 *
	 * @return ultimul batch disponibil
	 */
	public Long selectLastBatch();

	/**
	 * Lista cu toate batch-urile existente
	 *
	 * @return
	 */
	public List<Batch> selectAllBatches();

	/**
	 * Selectie de vehicle owner in functie de cartea de identitate
	 *
	 * @param roIdCard
	 *            cartea de identitate
	 * @return lista cu toti detinatorii de permis cu aceasta carte de identitate
	 */
	public List<VehicleOwner> selectVehicleOwnerByRoIdCard(String roIdCard);

	/**
	 * Selectie batch dupa id
	 *
	 * @param id
	 *            identificator unic de cautare in tabela de batch
	 * @return batch-ul cu id-ul din parametru
	 */
	public Batch selectBatchById(Long id);

	/**
	 * Stergere vehicle owner dupa id
	 *
	 * @param id
	 *            identificator de selectie in tabela de vehicle owners
	 */
	public void deleteVehicleOwnerById(Long id);

	/**
	 * Numararea batch-urilor existente
	 *
	 * @return numarul de batch-uri existente
	 */
	public Integer countBatches();

}