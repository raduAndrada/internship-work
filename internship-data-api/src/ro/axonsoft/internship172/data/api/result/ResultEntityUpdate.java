package ro.axonsoft.internship172.data.api.result;

import java.time.Instant;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface ResultEntityUpdate {
	@Nullable
	Integer getOddToEvenRatio();

	@Nullable
	Integer getPassedRegChangeDueDate();

	@Nullable
	Instant getResultProcessTime();

	@Nullable
	String getBatchId();

	@Nullable
	ResultEntityCriteria getCriteria();
}
