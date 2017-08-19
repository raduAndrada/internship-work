package ro.axonsoft.internship.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ro.axonsoft.internship172.impl.RoIdCardSeriesJudMapperImpl;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardSeriesException;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.RoIdCardSeriesJudMapper;
import ro.axonsoft.internship172.model.base.RoIdCardSeriesMapperUtil;

public class RoIdCardSeriesJudMapperTest {

    @Rule
    public final ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final RoIdCardSeriesJudMapper subject = new RoIdCardSeriesJudMapperImpl(
            RoIdCardSeriesMapperUtil.JUD_TO_SERIES_MAP);

    /**
     * Excepție așteptată când seria este goală.
     */
    @Test
    public void err_empty() throws InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardSeriesException.class);
        subject.mapIdCardToJud("");
    }

    /**
     * Excepție așteptată când seria este necunoscută.
     */
    @Test
    public void err_unk() throws InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardSeriesException.class);
        subject.mapIdCardToJud("XX");
    }

    /**
     * Success pentru o serie cunoscută scrisă cu litere mari.
     */
    @Test
    public void suc_upper() throws InvalidRoIdCardSeriesException {

        assertThat(subject.mapIdCardToJud("KX")).isEqualTo(Judet.CJ);
    }

    /**
     * Success pentru o serie cunoscută scrisă cu litere mici.
     */
    @Test
    public void suc_lower() throws InvalidRoIdCardSeriesException {
        assertThat(subject.mapIdCardToJud("kx")).isEqualTo(Judet.CJ);
    }

}