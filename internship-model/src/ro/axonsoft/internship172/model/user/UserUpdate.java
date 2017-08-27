package ro.axonsoft.internship172.model.user;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImtUserUpdate.Builder.class)
public interface UserUpdate {

    UserUpdateProperties getProperties();

    String getUsername();
}
