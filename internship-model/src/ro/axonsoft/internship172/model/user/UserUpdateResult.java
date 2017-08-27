package ro.axonsoft.internship172.model.user;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtUserUpdateResult.class)
@JsonDeserialize(builder = ImtUserUpdateResult.Builder.class)
public interface UserUpdateResult {

    String getUsername();

    UserUpdateProperties getProperties();

}
