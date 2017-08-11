package ro.axonsoft.internship172.data.domain;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

/**
 * Model pentru extragerea unui obiect din tabela de BATCH din baza de date
 * @author Andrada
 */
@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
public interface Batch {

	@Parameter
	@Nullable
	public Long getBatchId() ;



}
