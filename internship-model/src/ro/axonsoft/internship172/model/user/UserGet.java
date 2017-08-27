package ro.axonsoft.internship172.model.user;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonDeserialize(builder = ImtUserGet.Builder.class)
public interface UserGet {

	@Nullable
	Pagination getPagination();

	List<UserSortCriterion> getSort();

	@Nullable
	String getUsername();

	@Nullable
	String getPassoword();

	@Nullable
	String getSearch();
}
