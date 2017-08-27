package ro.axonsoft.internship172.data.api.user;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface UserEntityCount {

    @Value.Parameter
    UserEntityCriteria getCriteria();

    @Nullable
    String getSearch();
}
