package ro.axonsoft.internship172.model.vehicleOwner;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonSerialize(as = ImtVehicleOwnerGetResult.class)
@JsonDeserialize(builder = ImtVehicleOwnerGetResult.Builder.class)
public interface VehicleOwnerGetResult {

	Integer getCount();

	@Nullable
	Integer getPageCount();

	@Nullable
	Pagination getPagination();

	List<VehicleOwnerBasicRecord> getList();
}
