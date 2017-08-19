package ro.axonsoft.internship172.data.api.result;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ro.axonsoft.internship172.model.result.ResultErrorRecord;
import ro.axonsoft.internship172.model.result.ResultUnregCarsCountByJudRecord;

public interface ResultMetricsDao {
	int addResult(ResultEntity resultEntity);

	int addUnregCars(@Param("unregCars") List<ResultUnregCarsCountByJudRecord> unregCars);

	int addErrors(@Param("errors") List<ResultErrorRecord> errors);

	int updateResult(ResultEntityUpdate update);

	int deleteResult(ResultEntityDelete delete);

	Integer countResult(ResultEntityCount count);

	List<ResultEntity> getResult(ResultEntityGet get);

	int deleteResultError(ResultErrorEntityDelete delete);

	int deleteResultUnregCarsCountByJud(ResultUnregCarsCountByJudEntityDelete delete);
}
