package ro.axonsoft.internship172.model.user;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(builder = ImtUserCreate.Builder.class)
public interface UserCreate {

	@Nullable
	User getBasic();

	@Nullable
	String getPassword();

}
