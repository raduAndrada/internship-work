package ro.axonsoft.internship172.web.services;

import java.util.List;

import ro.axonsoft.internship172.data.domain.ResultError;
import ro.axonsoft.internship172.data.domain.ResultMetrics;
import ro.axonsoft.internship172.data.domain.ResultUnregCarsCountByJud;
import ro.axonsoft.internship172.data.exceptions.DatabaseIntegrityViolationException;
import ro.axonsoft.internship172.data.exceptions.InvalidDatabaseAccessException;
import ro.axonsoft.internship172.data.mappers.ErrorCriteria;
import ro.axonsoft.internship172.data.mappers.PageCriteria;
import ro.axonsoft.internship172.data.mappers.ResultUnregCarsCriteria;

public interface ResultRestService {
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

    public ResultMetrics selectResultMetricsById(Long id) throws InvalidDatabaseAccessException;

    /**
     * Selectarea tuturor rezultatelor care nu contin o lista de erori
     *
     * @return lista de rezultate fara erori
     */

    public List<ResultMetrics> findAllResults();

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
     * Metoda de inserare a unui rezultat continand datele pentru celalalte
     * tabele
     *
     * @param resultMetrics
     *            rezultatul pentru tabela de metrice
     * @param errorCriteria
     *            criteriu care se verifica pentru a insera/nu date in tabela de
     *            erori
     * @param unregCriteria
     *            criteriu de inserare pentru masinile neinregistrate
     */
    public void insertResultMetricsWithErrors(final ResultMetrics resultMetrics, final ErrorCriteria errorCriteria,
            final ResultUnregCarsCriteria unregCriteria)
            throws DatabaseIntegrityViolationException;

    public void updateResultByResultMetricsId(ResultMetrics resultMetrics);

    public void deleteResult(Long id);

    public List<ResultMetrics> getResultMetricsByBatchId(Long id);

    public Integer countResultMetricsByBatchId(Long id);

    public List<ResultMetrics> getResultMetricsPage(PageCriteria pageCriteria);
}
