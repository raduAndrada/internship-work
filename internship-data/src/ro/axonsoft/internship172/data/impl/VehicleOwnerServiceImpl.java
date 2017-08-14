package ro.axonsoft.internship172.data.impl;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.ImtVehicleOwnerCriteria;
import ro.axonsoft.internship172.data.mappers.PageCriteria;
import ro.axonsoft.internship172.data.mappers.VehicleOwnersDao;
import ro.axonsoft.internship172.data.services.VehicleOwnerService;
import ro.axonsoft.internship172.model.base.Batch;

/**
 * Serviciu pentru tabela de inregistrari de intrare aceleasi metode ca si
 * mapper-ul acestei tabele
 *
 * @author Andrada
 *
 */

public class VehicleOwnerServiceImpl implements VehicleOwnerService {

    private VehicleOwnersDao vehicleOwnersDao;

    @Inject
    public void setVehicleOwnersDao(final VehicleOwnersDao vehicleOwnersDao) {
        this.vehicleOwnersDao = vehicleOwnersDao;
    }

    @Override
    public VehicleOwner selectVehicleOwnerById (final Long id) throws InvalidDatabaseAccessException{
		final VehicleOwner temp = vehicleOwnersDao.selectVehicleOwnerById(id);
		if (temp == null ){
		    throw new InvalidDatabaseAccessException("id-ul specificat nu exista");
		} else {
            return temp;
        }


	}

    @Override
    public Integer countVehicleOwner() {

        return vehicleOwnersDao.countVehicleOwner(ImtVehicleOwnerCriteria.of());
    }

    @Override
    public void insertBatch(final Batch batch) {
        vehicleOwnersDao.insertBatch(batch);
    }

    @Override
    public Long selectLastBatch() {
        return vehicleOwnersDao.selectLastBatch();
    }

    @Override
    public List<VehicleOwner> getVehicleOwnersPage(final PageCriteria pageCriteria) {
        return vehicleOwnersDao.getVehicleOwnersPage(pageCriteria);
    }

    @Override
    public void insertVehicleOwner(final VehicleOwner vehicleOwner) throws DatabaseIntegrityViolationException {
        final List<VehicleOwner> temp = Lists.newArrayList(vehicleOwnersDao.selectVehicleOwnerByRoIdCard(vehicleOwner.getRoIdCard()));
        if (temp != null) {
            if (temp.size() != 0) {
                throw new DatabaseIntegrityViolationException(
                    "cartea de identitate este deja inregistrata");
            }
        }
        if (vehicleOwner.getVehicleOwnerId() != null) {
            final VehicleOwner tmp = vehicleOwnersDao.selectVehicleOwnerById(vehicleOwner.getVehicleOwnerId());
            if (tmp != null) {
                throw new DatabaseIntegrityViolationException(
                        "id-ul specificat apartine altei persoane");
            }
        }
        if (vehicleOwnersDao.selectBatchById(vehicleOwner.getBatchId()) == null)
        {
            throw new DatabaseIntegrityViolationException(
                    "batch-ul nu exista");
        }
        vehicleOwnersDao.insertVehicleOwner(vehicleOwner);
    }

    @Override
    public Iterable<Batch> selectAllBatches() {
        return vehicleOwnersDao.selectAllBatches();
    }

    @Override
    public Iterable<VehicleOwner> selectVehicleOwnerByRoIdCard(final String roIdCard) {
        return vehicleOwnersDao.selectVehicleOwnerByRoIdCard(roIdCard);
    }

    @Override
    public Batch selectBatchById(final Long id) throws InvalidDatabaseAccessException{
        final Batch temp = vehicleOwnersDao.selectBatchById(id);
        if (temp == null)
        {
            throw new InvalidDatabaseAccessException("Batch-ul specificat nu exista");

        } else {
            return temp;
        }
    }

    @Override
    public void deleteVehicleOwnerById (final Long id) throws DatabaseIntegrityViolationException{
        final VehicleOwner temp = selectVehicleOwnerById(id);
        if (temp == null)
        {
            throw new DatabaseIntegrityViolationException("inregistrarea nu exista");
        }
        vehicleOwnersDao.deleteVehicleOwnerById(id);

    }

    @Override
    public Integer countVehicleOwnersByBatchId(final Long id) {
        return vehicleOwnersDao.countVehicleOwnersByBatchId(id);
    }

    @Override
    public Integer countBatches() {
        return vehicleOwnersDao.countBatches();
    }

    @Override
    public List<Batch> getBatchPage(final PageCriteria pageCriteria) {
        return vehicleOwnersDao.getBatchPage(pageCriteria);
    }

}