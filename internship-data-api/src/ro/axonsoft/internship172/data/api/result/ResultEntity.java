package ro.axonsoft.internship172.data.api.result;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import ro.axonsoft.internship172.model.result.ResultRecord;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
public interface ResultEntity {

	@Nullable
	ResultRecord getRecord();

	@Nullable
	Long getResultMetricsId();

}
