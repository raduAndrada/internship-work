package ro.axonsoft.internship172.model.result;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtResultUnregCarsCountByJudDeleteResult.class)
@JsonDeserialize(builder = ImtResultUnregCarsCountByJudDeleteResult.Builder.class)
public interface ResultUnregCarsCountByJudDeleteResult {
	ResultUnregCarsCountByJudBasic getBasic();
}
