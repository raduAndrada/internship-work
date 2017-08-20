package ro.axonsoft.internship172.model.batch;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.model.base.Batch;

@Value.Immutable
@JsonSerialize(as = ImtBatchCreateResult.class)
@JsonDeserialize(builder = ImtBatchCreateResult.Builder.class)
public interface BatchCreateResult {

	Batch getBatch();
}
