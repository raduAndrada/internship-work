package ro.axonsoft.internship172.model.user;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Pagination;

@Value.Immutable
@JsonSerialize(as = ImtUserGetResult.class)
@JsonDeserialize(builder = ImtUserGetResult.Builder.class)
public interface UserGetResult {

	Integer getCount();

	@Nullable
	Integer getPageCount();

	@Nullable
	Pagination getPagination();

	List<UserRecord> getList();
}
