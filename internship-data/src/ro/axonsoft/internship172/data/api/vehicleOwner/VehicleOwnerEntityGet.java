package ro.axonsoft.internship172.data.api.vehicleOwner;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.vehicleOwner.VehicleOwnerSortCriterion;

@Value.Immutable
public interface VehicleOwnerEntityGet {

	List<VehicleOwnerSortCriterion> getSort();

	@Nullable
	Pagination getPagination();

	@Nullable
	VehicleOwnerEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
