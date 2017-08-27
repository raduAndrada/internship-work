package ro.axonsoft.internship172.model.result;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImtResultMetricsDeleteResult.class)
@JsonDeserialize(builder = ImtResultMetricsDeleteResult.Builder.class)
public interface ResultMetricsDeleteResult {
	ResultBasic getBasic();
}
