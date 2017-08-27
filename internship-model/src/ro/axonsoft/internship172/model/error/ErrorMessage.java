package ro.axonsoft.internship172.model.error;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Serial.Version(1)
@JsonSerialize(as = ImtErrorMessage.class)
@JsonDeserialize(as = ImtErrorMessage.class)
public interface ErrorMessage {

    String getMessage();

    @Nullable
    ErrorProperties getProperties();
}
