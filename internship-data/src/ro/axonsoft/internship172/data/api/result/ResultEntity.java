package ro.axonsoft.internship172.data.api.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import ro.axonsoft.internship172.model.result.ResultErrorRecord;
import ro.axonsoft.internship172.model.result.ResultRecord;
import ro.axonsoft.internship172.model.result.ResultUnregCarsCountByJudRecord;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
public interface ResultEntity {

	@Nullable
	ResultRecord getRecord();

	@Nullable
	Long getResultMetricsId();

	@Nullable
	List<ResultUnregCarsCountByJudRecord> getUnregCars();

	@Nullable
	List<ResultErrorRecord> getErrors();
}
