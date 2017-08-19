package ro.axonsoft.internship172.model.result;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.ResultBatch;

@Value.Immutable
@JsonDeserialize(builder = ImtResultCreate.Builder.class)
public interface ResultCreate {
	ResultBasic getBasic();

	@Nullable
	ResultBatch getBatch();

	@Nullable
	List<ResultUnregCarsCountByJudRecord> getUnregCars();

	@Nullable
	List<ResultErrorRecord> getErrors();
}
