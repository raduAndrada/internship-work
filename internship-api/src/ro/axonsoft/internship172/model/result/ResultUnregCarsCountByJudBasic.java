package ro.axonsoft.internship172.model.result;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ro.axonsoft.internship172.api.Judet;

@Value.Modifiable
@Value.Immutable
@Serial.Version(1)
@JsonSerialize(as = ImtResultUnregCarsCountByJudBasic.class)
@JsonDeserialize(builder = ImtResultUnregCarsCountByJudBasic.Builder.class)
public interface ResultUnregCarsCountByJudBasic {
	@Nullable
	Judet getJudet();

	@Nullable
	Integer getUnregCarsCount();

}
