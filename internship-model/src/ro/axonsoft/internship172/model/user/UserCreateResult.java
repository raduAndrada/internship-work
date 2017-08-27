package ro.axonsoft.internship172.model.user;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtUserCreateResult.class)
@JsonDeserialize(builder = ImtUserCreateResult.Builder.class)
public interface UserCreateResult {

    User getBasic();

    @Nullable
    String getPassword();

}
