package ro.axonsoft.internship172.business.api.vehicleOwner;

import ro.axonsoft.internship172.model.batch.BatchCreate;
import ro.axonsoft.internship172.model.batch.BatchCreateResult;
import ro.axonsoft.internship172.model.batch.BatchGet;
import ro.axonsoft.internship172.model.batch.BatchGetResult;
import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreate;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerCreateResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerDeleteResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGet;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerGetResult;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerUpdate;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerUpdateResult;

public interface VehicleOwnerBusiness {

	VehicleOwnerCreateResult createVehicleOwner(VehicleOwnerCreate vhoCreate) throws BusinessException;

	VehicleOwnerUpdateResult updateVehicleOwner(VehicleOwnerUpdate vhoUpdate) throws BusinessException;

	VehicleOwnerDeleteResult deleteVehicleOwner(String roIdCard) throws BusinessException;

	VehicleOwnerGetResult getVehicleOwners(VehicleOwnerGet vhoGet);

	BatchCreateResult createBatch(BatchCreate batchCreate) throws BusinessException;

	BatchGetResult getBatches(BatchGet batchGet);

}
