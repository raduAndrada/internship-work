package ro.axonsoft.internship172.model.base;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Model pentru extragerea unui obiect din tabela de BATCH din baza de date
 *
 * @author Andrada
 */
@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = MdfBatch.class)
@JsonDeserialize(as = MdfBatch.class)
public interface Batch {

	@Value.Parameter
	@Nullable
	public Long getBatchId();

}
