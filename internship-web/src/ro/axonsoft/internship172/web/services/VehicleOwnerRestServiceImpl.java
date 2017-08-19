package ro.axonsoft.internship172.web.services;

import java.util.List;

import javax.inject.Inject;

import ro.axonsoft.internship172.data.domain.VehicleOwner;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.PageCriteria;
import ro.axonsoft.internship172.data.services.VehicleOwnerService;
import ro.axonsoft.internship172.model.base.ResultBatch;

public class VehicleOwnerRestServiceImpl implements VehicleOwnerRestService
{

    VehicleOwnerService vehicleOwnerService;

    @Inject
    public void setVehicleOwnerService(final VehicleOwnerService vehicleOwnerService) {
        this.vehicleOwnerService = vehicleOwnerService;
    }

    @Override
    public VehicleOwner selectVehicleOwnerById(final Long id) throws InvalidDatabaseAccessException {
        return vehicleOwnerService.selectVehicleOwnerById(id);
    }

    @Override
    public List<VehicleOwner> getVehicleOwnersPage(final PageCriteria pageCriteria) throws DatabaseIntegrityViolationException {

        return vehicleOwnerService.getVehicleOwnersPage(pageCriteria);
    }

    @Override
    public void insertVehicleOwner(final VehicleOwner vehicleOwner) throws DatabaseIntegrityViolationException {
        vehicleOwnerService.insertVehicleOwner(vehicleOwner);

    }

    @Override
    public Integer countVehicleOwner() {
        return vehicleOwnerService.countVehicleOwner();
    }

    @Override
    public void insertBatch(final ResultBatch batch) {
        vehicleOwnerService.insertBatch(batch);

    }

    @Override
    public Long selectLastBatch() {
        return vehicleOwnerService.selectLastBatch();
    }

    @Override
    public Iterable<ResultBatch> selectAllBatches() {
        return vehicleOwnerService.selectAllBatches();
    }

    @Override
    public Iterable<VehicleOwner> selectVehicleOwnerByRoIdCard(final String roIdCard) {
        return vehicleOwnerService.selectVehicleOwnerByRoIdCard(roIdCard);
    }

    @Override
    public ResultBatch selectBatchById(final Long id) throws InvalidDatabaseAccessException {
        return vehicleOwnerService.selectBatchById(id);
    }

    @Override
    public void deleteVehicleOwnerById(final Long id) throws DatabaseIntegrityViolationException {
        vehicleOwnerService.deleteVehicleOwnerById(id);

    }

    @Override
    public Integer countVehicleOwnersByBatchId(final Long id) {
        return vehicleOwnerService.countVehicleOwnersByBatchId(id);
    }

    @Override
    public Integer countBatches() {
        return vehicleOwnerService.countBatches();
    }

    @Override
    public List<ResultBatch> getBatchPage(final PageCriteria pageCriteria) {
        return vehicleOwnerService.getBatchPage(pageCriteria);
    }




}
