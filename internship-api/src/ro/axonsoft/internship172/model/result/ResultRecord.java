package ro.axonsoft.internship172.model.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.ResultBatch;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
@JsonSerialize(as = ImtResultRecord.class)
@JsonDeserialize(builder = ImtResultRecord.Builder.class)
public interface ResultRecord {

	@Nullable
	ResultBasic getBasic();

	@Nullable
	ResultBatch getBatch();

	@Nullable
	List<ResultUnregCarsCountByJudRecord> getUnregCars();

	@Nullable
	List<ResultErrorRecord> getErrors();
}
