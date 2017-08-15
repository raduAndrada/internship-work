package ro.axonsoft.internship172.model.result;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = ImtResultUnregCarsCountByJudRecord.class)
@JsonDeserialize(builder = ImtResultUnregCarsCountByJudRecord.Builder.class)
public interface ResultUnregCarsCountByJudRecord {
	@Nullable
	ResultUnregCarsCountByJudBasic getBasic();

	@Nullable
	Long getResultMetricsId();

	@Nullable
	Long getUnregCarsCountId();
}
