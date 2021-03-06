package ro.axonsoft.internship172.data.api.result;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

@Value.Immutable
@Serial.Version(1)
public interface ResultEntityCriteria {
	Set<Long> getIdIncl();

	Set<Long> getIdBatchSelect();

	@Nullable
	Set<Long> getIdResultSelect();

}
