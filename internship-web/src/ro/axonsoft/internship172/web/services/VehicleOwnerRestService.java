package ro.axonsoft.internship172.web.services;

import java.util.List;

import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.PageCriteria;
import ro.axonsoft.internship172.model.base.Batch;

public interface VehicleOwnerRestService {

        /**
         * Selecteaza un vehicleOwner din baza de date
         *
         * @param id
         *            id-ul inregistrarii
         * @return un obiect de tip VehicleOwner cu toate campurile
         */
        public VehicleOwner selectVehicleOwnerById(Long id) throws InvalidDatabaseAccessException;

        /**
         * MEtoda care selecteaza toti VehicleOwnerii din baza de date
         *
         * @return o lista cu VehicleOwnerii
         */
        public List<VehicleOwner> getVehicleOwnersPage (
                PageCriteria pageCriteria) throws DatabaseIntegrityViolationException;

        /**
         * Inserarea unei noi inregistrari
         *
         * @param vehicleOwner
         *            obiect cu datele pentru baza de date
         */
        public void insertVehicleOwner(VehicleOwner vehicleOwner) throws DatabaseIntegrityViolationException;

        /**
         * Returneaza nuamrul de inregistrari din baza de date pentru VehicleOwner
         *
         * @param criteria
         * @return numarul de inregistrari din tabela
         */

        public Integer countVehicleOwner();

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
         *
         * @return toate batch-urile existente in tabela de BATCH
         */
        public Iterable<Batch> selectAllBatches();

        /**
         * Selectarea unei liste de detinatori de permise in functie de cartea lor de identitate
         * @param roIdCard String cu datele cartii de identiate
         * @return detinatorii cartilor de identitate
         */
        public Iterable<VehicleOwner> selectVehicleOwnerByRoIdCard(String roIdCard);

        public Batch selectBatchById(Long id) throws InvalidDatabaseAccessException;

        public void deleteVehicleOwnerById(Long id) throws DatabaseIntegrityViolationException;

        public Integer countVehicleOwnersByBatchId(Long id);

        public Integer countBatches();

        public List<Batch> getBatchPage(PageCriteria pageCriteria);



}
