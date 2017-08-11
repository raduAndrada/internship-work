package ro.axonsoft.internship.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ro.axonsoft.internship172.api.ImtRoRegPlateProperties;
import ro.axonsoft.internship172.api.InvalidRoRegPlateException;
import ro.axonsoft.internship172.api.Judet;
import ro.axonsoft.internship172.api.RoRegPlateParser;
import ro.axonsoft.internship172.api.RoRegPlateProperties;
import ro.axonsoft.internship172.impl.RoRegPlateParserImpl;

public class RoRegPlateParserTest {

    @Rule
    public final ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final RoRegPlateParser subject = new RoRegPlateParserImpl();

    /**
     * Excepție așteptată când numărul de înmatriculare este gol.
     */
    @Test
    public void err_empty() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("");
    }

    /**
     * Excepție așteptată când numărul de litere e prea mic.
     */
    @Test
    public void err_letters_len_short() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("CJ10AD");
    }

    /**
     * Excepție așteptată când numărul de litere e prea mare.
     */
    @Test
    public void err_letters_len_long() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("CJ10ADXX");
    }

    /**
     * Excepție așteptată când în secțiunea dedicată literelor există cifre.
     */
    @Test
    public void err_letters_contain_digits() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("CJ10AD0");
    }

    /**
     * Excepție așteptată când în secțiunea dedicată literelor există caractere
     * non-latine.
     */
    @Test
    public void err_letters_contain_nonLatin1() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("CJ10ADȚ");
    }

    /**
     * Excepție așteptată când numărul de cifre e prea mic.
     */
    @Test
    public void err_digits_len_short() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("CJ1ADC");
    }

    /**
     * Excepție așteptată când numărul de cifre e prea mare pentru alte județe
     * decât București.
     */
    @Test
    public void err_digits_len_long() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("CJ100ADC");
    }

    /**
     * Excepție așteptată când numărul de cifre e prea mare pentru București.
     */
    @Test
    public void err_digits_len_long_b() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("B1000ADC");
    }

    /**
     * Excepție așteptată când județul nu este cunoscut.
     */
    @Test
    public void err_jud_unk() throws InvalidRoRegPlateException {
        expectedException.expect(InvalidRoRegPlateException.class);
        subject.parseRegistrationPlate("XX10ADC");
    }

    /**
     * Succes pentru un număr corect, toate literele majuscule, fără spații.
     */
    @Test
    public void suc_uppper_nospace() throws InvalidRoRegPlateException {
        final RoRegPlateProperties res = subject.parseRegistrationPlate("CJ10ADC");
        assertThat(res.getJudet()).isEqualTo(Judet.CJ);
        assertThat(res.getLetters()).isEqualTo("ADC");
        assertThat(res.getDigits()).isEqualTo((short) 10);
    }

    /**
     * Succes pentru un număr corect din București, toate literele majuscule,
     * fără spații, 2 cifre.
     */
    @Test
    public void suc_uppper_nospace_b() throws InvalidRoRegPlateException {
        final RoRegPlateProperties res = subject.parseRegistrationPlate("B10ADC");
        assertThat(res.getJudet()).isEqualTo(Judet.B);
        assertThat(res.getLetters()).isEqualTo("ADC");
        assertThat(res.getDigits()).isEqualTo((short) 10);
    }

    /**
     * Succes pentru un număr corect din București, toate literele majuscule,
     * fără spații, 3 cifre.
     */
    @Test
    public void suc_uppper_nospace_b3() throws InvalidRoRegPlateException {
        final RoRegPlateProperties res = subject.parseRegistrationPlate("B100ADC");
        assertThat(res.getJudet()).isEqualTo(Judet.B);
        assertThat(res.getLetters()).isEqualTo("ADC");
        assertThat(res.getDigits()).isEqualTo((short) 100);
    }

    /**
     * Succes pentru un număr corect format din litere mici, fără spații.
     */
    @Test
    public void suc_lower_nospace() throws InvalidRoRegPlateException {
        final RoRegPlateProperties res = subject.parseRegistrationPlate("cj10adc");
        assertThat(ImtRoRegPlateProperties.copyOf(res)).isEqualTo(ImtRoRegPlateProperties.builder()
                .judet(Judet.CJ)
                .letters("ADC")
                .digits((short) 10)
                .build());
    }

    /**
     * Succes pentru un număr corect format din litere mari, cu spații.
     */
    @Test
    public void suc_upper_spaces() throws InvalidRoRegPlateException {
        final RoRegPlateProperties res = subject.parseRegistrationPlate(" CJ  10  ADC   ");
        assertThat(res.getJudet()).isEqualTo(Judet.CJ);
        assertThat(res.getLetters()).isEqualTo("ADC");
        assertThat(res.getDigits()).isEqualTo((short) 10);
    }

    /**
     * Succes pentru toate judelete.
     */
    @Test
    public void suc_jud_all() throws InvalidRoRegPlateException {
        final Set<String> juds = new TreeSet<>(Arrays.asList("AB", "AR", "AG", "BC", "BH", "BN", "BT", "BV", "BR", "BZ", "CS", "CJ",
                "CT", "CV", "DB", "DJ", "GL", "GJ", "HR", "HD", "IL", "IS", "IF", "MM", "MH", "MS", "NT", "OT", "PH", "SM", "SJ", "SB",
                "SV", "TR", "TM", "TL", "VS", "VL", "VN", "B", "CL", "GR"));
        for (final String jud : juds) {
            final String plate = jud + "10ADC";
            final RoRegPlateProperties res = subject.parseRegistrationPlate(plate);
            assertThat(res.getJudet().toString()).isEqualTo(jud);
            assertThat(res.getLetters()).isEqualTo("ADC");
            assertThat(res.getDigits()).isEqualTo((short) 10);
        }
    }
}
