package ro.axonsoft.internship172.data.api.user;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import ro.axonsoft.internship172.model.user.UserRecord;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
public interface UserEntity {

	@Nullable
	UserRecord getRecord();

	@Nullable
	Long getId();

	@Nullable
	String getPassword();
}
