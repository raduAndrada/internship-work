package ro.axonsoft.internship172.data.mappers;

import java.util.List;

import ro.axonsoft.internship172.data.domain.ResultError;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.domain.ResultUnregCarsCountByJud;
import ro.axonsoft.internship172.model.base.ResultBatch;

/**
 * Mapper pentru trabela de metrice de rezultate
 *
 * @author intern
 *
 */
public interface ResultDao {

	/**
	 * Metoda de inserare in tabela de metrice
	 *
	 * @param metrics
	 *            noile date inserate in baza de date
	 * @return
	 */
	public int insertResultMetrics(ResultMetrics metrics);

	/**
	 * Selectarea unui rezultat dupa id
	 *
	 * @param id
	 *            parametrul dupa care se face selectia
	 * @return rezultatul dorit
	 */

	public ResultMetrics selectResultMetricsById(Long id);

	/**
	 * Metoda de inserare erori
	 *
	 * @param resultErrors
	 *            lista de erori aparute la procesare
	 * @param resultMetricsId
	 *            id-ul metricei rezultat de care tine lista
	 * @param errorCriteria
	 *            criteriu care trebuie indeplinit pentru a insera o eroare
	 */
	public void insertResultError(ResultError resultError);

	/**
	 * Inserarea unui rezultat cu masinile neinregistrate pe judet
	 *
	 * @param resultUnregCarsCountByJud
	 *            numarul de masini neinregistrate si judetul de care tin ele
	 * @param resultMetricsId
	 *            metricea rezultat de care apartine lista
	 */
	public void insertResultUnregCarsCountByJud(ResultUnregCarsCountByJud resultUnregCarsCountByJud);

	/**
	 * Selectarea unei liste de erori in functie de rezultatul de care apartin
	 *
	 * @param id
	 *            cheia straina dupa care se face cautarea in baza de date
	 * @return lista cu erorile care tin de rezultatul selectat
	 */
	public List<ResultError> selectResultErrorByMetricsId(Long id);

	/**
	 * Selectarea unei liste din tabela de masini neinregistrate in functie de
	 * id-ul rezultatului de care apartin
	 *
	 * @param id
	 *            cheia dupa care se face cautarea
	 * @return o lista de masini neinregistrate
	 */
	public List<ResultUnregCarsCountByJud> selectResultUnregCarsCountByMetricsId(Long id);

	/**
	 * Selectarea tuturor rezultatelor din baza de date
	 *
	 * @return o lista cu toate datele din tabela RESULT_METRICS impreuna cu
	 *         lista de erori si de masini neinregistrate
	 */
	public List<ResultMetrics> selectAllResultMetrics();

	/**
	 * Metoda de update pentru rezultate
	 * 
	 * @param resultMetrics
	 *            metricea de updatat
	 */

	public void updateResultMetricsById(ResultMetrics resultMetrics);

	/**
	 * Update pentru tabela de masini neinregistrate
	 * 
	 * @param resultUnregCarsCountByJud
	 */

	public void updateResultUnregCarsByMetricsId(ResultUnregCarsCountByJud resultUnregCarsCountByJud);

	/**
	 * Update pentru tabela de erori
	 * 
	 * @param resultError
	 *            eroare de updatat
	 */
	public void updateResulErrorsByMetricsId(ResultError resultError);

	/**
	 * Lista cu metricele rezultat in functie de id-ul de batch
	 * 
	 * @param id
	 *            batch-id-ul dupa care se face selectia
	 * @return lista metricelor rezultat cu acest batch id
	 */
	public List<ResultMetrics> getResultMetricsByBatchId(Long id);

	/**
	 * Selctie batch in functie de id
	 * 
	 * @param id
	 *            identificatorul batch-ului
	 * @return obiectul cu id-ul selectat
	 */
	public ResultBatch selectBatchById(Long id);

	/**
	 * Stergere metrice dupa id
	 * 
	 * @param id
	 *            identificatorul de selectie al stergerii
	 */
	public void deleteResultErrosByMetricsId(Long id);
	
	/**
	 * Stergere rezultat din tabela de masini neinregistrate 
	 * @param id identificato unic de recunoasterea inregistrari
	 */

	public void deleteResultUnregCarsCountByMetricsId(Long id);

	/**
	 * Sterge rezultat in functie de id-ul metricei
	 * @param id cheia primara din tabela metricelor rezultat
	 */
	public void deleteResultMetricsById(Long id);

	/**
	 * Numara datele inregistrate in tabela de metrici rezultat
	 * @param identificatorul de batch pentru rezultatele cautate
	 * @return
	 */
	public Integer countResultMetricsByBatchId(Long id);

	/**
	 * Paginare 
	 * @param pageCriteria criteriu de selectie
	 * @return pagina selectata
	 */
	public List<ResultMetrics> getResultMetricsPage(PageCriteria pageCriteria);

}
