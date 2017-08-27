package ro.axonsoft.internship172.model.user;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = ImtUserRecord.class)
@JsonDeserialize(builder = ImtUserRecord.Builder.class)
public interface UserRecord {

	@Nullable
	User getBasic();

}
