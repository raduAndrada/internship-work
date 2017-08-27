package ro.axonsoft.internship172.data.api.user;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface UserEntityUpdate {

	@Nullable
	String getFirstName();

	@Nullable
	String getLastName();

	@Nullable
	String getPassword();

	@Nullable
	String getEmail();

	@Nullable
	UserEntityCriteria getCriteria();
}
