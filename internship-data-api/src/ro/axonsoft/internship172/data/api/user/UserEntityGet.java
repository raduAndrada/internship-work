package ro.axonsoft.internship172.data.api.user;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import ro.axonsoft.internship172.model.base.Pagination;
import ro.axonsoft.internship172.model.user.UserSortCriterion;

@Value.Immutable
public interface UserEntityGet {

	List<UserSortCriterion> getSort();

	@Nullable
	Pagination getPagination();

	@Nullable
	UserEntityCriteria getCriteria();

	@Nullable
	String getSearch();
}
