package ro.axonsoft.internship172.model.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Batch;

@Value.Immutable
@JsonDeserialize(builder = ImtResultMetricsCreateResult.Builder.class)
public interface ResultMetricsCreateResult {
	ResultBasic getBasic();

	@Nullable
	Batch getBatch();

	@Nullable
	List<ResultUnregCarsCountByJudRecord> getUnregCars();

	@Nullable
	List<ResultErrorRecord> getErrors();
}
