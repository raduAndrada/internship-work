package ro.axonsoft.internship172.data.api.vehicleOwner;

import java.util.List;

import ro.axonsoft.internship172.data.api.batch.BatchEntityCount;
import ro.axonsoft.internship172.data.api.batch.BatchEntityGet;
import ro.axonsoft.internship172.model.base.ResultBatch;

public interface VehicleOwnerDao {
	int addVehicleOwner(VehicleOwnerEntity vho);

	int addBatch(ResultBatch batch);

	int updateVehicleOwner(VehicleOwnerEntityUpdate update);

	int deleteVehicleOwner(VehicleOwnerEntityDelete delete);

	Integer countVehicleOwner(VehicleOwnerEntityCount count);

	List<VehicleOwnerEntity> getVehicleOwner(VehicleOwnerEntityGet get);

	List<ResultBatch> getBatch(BatchEntityGet get);

	Integer countBatch(BatchEntityCount count);
}
