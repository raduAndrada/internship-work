package ro.axonsoft.internship.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import ro.axonsoft.internship172.impl.RoIdCardParserImpl;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardException;
import ro.axonsoft.internship172.model.api.InvalidRoIdCardSeriesException;
import ro.axonsoft.internship172.model.api.Judet;
import ro.axonsoft.internship172.model.api.RoIdCardParser;
import ro.axonsoft.internship172.model.api.RoIdCardProperties;
import ro.axonsoft.internship172.model.api.RoIdCardSeriesJudMapper;

@RunWith(EasyMockRunner.class)
public class RoIdCardParserTest {

    @Rule
    public final ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private RoIdCardSeriesJudMapper roIdCardSeriesJudMapper;

    private RoIdCardParser subject;

    @Before
    public void beforeEachTest() {
        subject = new RoIdCardParserImpl(roIdCardSeriesJudMapper);
        reset(roIdCardSeriesJudMapper);
    }

    /**
     * Excepție așteptată când CI este gol.
     */
    @Test
    public void err_empty() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("");
    }

    /**
     * Excepție așteptată când CI are prea puține litere.
     */
    @Test
    public void err_letters_len_short() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("K")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("K1")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("K112233");
    }

    /**
     * Excepție așteptată când CI are prea multe.
     */
    @Test
    public void err_letters_len_long() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KK")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KKK")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("KKK112233");
    }

    /**
     * Excepție așteptată când CI are prea multe cifre și prea puține litere.
     */
    @Test
    public void err_letters_contains_digits() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("K")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("K1")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("K1112233");
    }

    /**
     * Excepție așteptată când CI are prea conține litere din afara alfabeltului
     * latin.
     */
    @Test
    public void err_letters_contains_nonLatin() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KȚ")).andStubThrow(new InvalidRoIdCardSeriesException(""));
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("KȚ112233");
    }

    /**
     * Excepție așteptată când CI are prea puține cifre.
     */
    @Test
    public void err_digits_len_short() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KX")).andStubReturn(Judet.CJ);
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("KX11122");
    }

    /**
     * Excepție așteptată când CI are prea multe cifre.
     */
    @Test
    public void err_digits_len_long() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KX")).andStubReturn(Judet.CJ);
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("KX1112233");
    }

    /**
     * Excepție așteptată când CI are prea multe cifre.
     */
    @Test
    public void err_digits_contains_letter() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KX")).andStubReturn(Judet.CJ);
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("KX11122A");
    }

    /**
     * Excepție așteptată când CI conține o serie invalidă.
     */
    @Test
    public void err_series_invalid() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expectedException.expect(InvalidRoIdCardException.class);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("XX")).andStubThrow(new InvalidRoIdCardSeriesException("Serie invalida"));
        replay(roIdCardSeriesJudMapper);
        subject.parseIdCard("XX112233");
    }

    /**
     * Success CI cu majuscule și fără spații.
     */
    @Test
    public void suc_upper_nospace() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KX")).andReturn(Judet.CJ);
        replay(roIdCardSeriesJudMapper);
        final RoIdCardProperties idCard = subject.parseIdCard("KX112233");
        assertThat(idCard.getJudet()).isEqualByComparingTo(Judet.CJ);
        assertThat(idCard.getSeries()).isEqualTo("KX");
        assertThat(idCard.getNumber()).isEqualTo(112233);
    }

    /**
     * Success CI cu majuscule și cu spații la început și la sfârșit.
     */
    @Test
    public void suc_upper_spaces() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KX")).andReturn(Judet.CJ);
        replay(roIdCardSeriesJudMapper);
        final RoIdCardProperties idCard = subject.parseIdCard(" KX112233  ");
        assertThat(idCard.getJudet()).isEqualByComparingTo(Judet.CJ);
        assertThat(idCard.getSeries()).isEqualTo("KX");
        assertThat(idCard.getNumber()).isEqualTo(112233);
    }

    /**
     * Success CI cu litere mici și fără spații.
     */
    @Test
    public void suc_lower_nospace() throws InvalidRoIdCardException, InvalidRoIdCardSeriesException {
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("KX")).andReturn(Judet.CJ);
        expect(roIdCardSeriesJudMapper.mapIdCardToJud("kx")).andReturn(Judet.CJ);
        replay(roIdCardSeriesJudMapper);
        final RoIdCardProperties idCard = subject.parseIdCard("kx112233");
        assertThat(idCard.getJudet()).isEqualByComparingTo(Judet.CJ);
        assertThat(idCard.getSeries()).isEqualTo("KX");
        assertThat(idCard.getNumber()).isEqualTo(112233);
    }

}
