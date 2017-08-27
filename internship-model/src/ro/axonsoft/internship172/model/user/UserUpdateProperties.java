package ro.axonsoft.internship172.model.user;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtUserUpdateProperties.class)
@JsonDeserialize(as = ImtUserUpdateProperties.class)
public interface UserUpdateProperties {
    String getFirstName();

    String getLastName();

    String getEmail();
}
