package ro.axonsoft.internship172.data.api.user;

import org.immutables.value.Value;

@Value.Immutable
public interface UserEntityDelete {

	UserEntityCriteria getCriteria();
}
