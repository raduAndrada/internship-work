package ro.axonsoft.internship172.data.domain;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.serial.Serial;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Serial.Version(1)
public interface ConcreteResultMetrics {
	/**
	 *
	 * @param oddToEvenRatio
	 *            datele de pe coloana ODD_TO_EVEN_RATIO
	 * @param passedRegChangeDueDate
	 *            datele de pe coloana PASSED_REG_CHANGE_DUE_DATE
	 * @param resultMetricsId
	 *            datele de pe coloana RESULT_METRICS_ID
	 * @param batchId
	 *            datele de pe coloana BATCH_ID
	 * @param unregCarsCountByJud
	 *            lista cu masinile neinregistrate pentru judete
	 * @param resultError
	 *            lista cu erorile pentru batch-ul de procesare
	 */

	@Nullable
	Integer getOddToEvenRatio();

	@Nullable
	Integer getPassedRegChangeDueDate();

	@Nullable
	Long getResultMetricsId();

	@Nullable
	List<MdfResultUnregCarsCountByJud> getUnregCarsCountByJud();

	@Nullable
	Long getBatchId();

	@Nullable
	List<MdfResultError> getResultErrors();

	@Nullable
	java.sql.Timestamp getResultProcessTime();
}
