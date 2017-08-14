package ro.axonsoft.internship172.model.vehicleOwner;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonDeserialize(builder = ImtVehicleOwnerGet.Builder.class)
public interface VehicleOwnerGet {

	@Nullable
	Pagination getPagination();

	List<VehicleOwnerSortCriterion> getSort();

	@Nullable
	String getRoIdCard();

	@Nullable
	Long getVehicleOwnerId();

	@Nullable
	Long getBatchId();

	@Nullable
	String getSearch();
}
