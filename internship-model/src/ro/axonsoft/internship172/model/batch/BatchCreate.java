package ro.axonsoft.internship172.model.batch;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ro.axonsoft.internship172.model.base.Batch;

@Value.Immutable
@JsonDeserialize(builder = ImtBatchCreate.Builder.class)
public interface BatchCreate {

	@Nullable
	Batch getBatch();
}
