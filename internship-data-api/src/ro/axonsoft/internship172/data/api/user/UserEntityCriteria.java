package ro.axonsoft.internship172.data.api.user;

import java.util.Set;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

@Value.Immutable
@Serial.Version(1)
public interface UserEntityCriteria {

    Set<String> getUsernameIncl();

    Set<Long> getIdIncl();

}
