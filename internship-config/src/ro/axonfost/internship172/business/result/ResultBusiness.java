package ro.axonfost.internship172.business.result;

import ro.axonsoft.internship172.model.error.BusinessException;
import ro.axonsoft.internship172.model.result.ResultCreate;
import ro.axonsoft.internship172.model.result.ResultGet;
import ro.axonsoft.internship172.model.result.ResultMetricsCreateResult;
import ro.axonsoft.internship172.model.result.ResultMetricsDeleteResult;
import ro.axonsoft.internship172.model.result.ResultMetricsGetResult;
import ro.axonsoft.internship172.model.result.ResultMetricsUpdateResult;
import ro.axonsoft.internship172.model.result.ResultUpdate;

public interface ResultBusiness {

	ResultMetricsCreateResult createResult(ResultCreate resCreate) throws BusinessException;

	ResultMetricsUpdateResult updateResult(ResultUpdate resUpdate) throws BusinessException;

	ResultMetricsDeleteResult deleteResult(Long id) throws BusinessException;

	ResultMetricsGetResult getResults(ResultGet resGet);

}
