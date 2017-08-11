package ro.axonsoft.internship.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import ro.axonsoft.internship172.api.InvalidRoRegPlateException;
import ro.axonsoft.internship172.api.Judet;
import ro.axonsoft.internship172.impl.RoRegPlateParserImpl;

public class RoRegPlateParserImplTest {

    private final RoRegPlateParserImpl parser = new RoRegPlateParserImpl();

    private final String regPlate = "B101BBB";
    private final String regPlate0 = " cj 01 cjc";
    private final String regPlate1 = "FS2DS3";

    @Test
    public void roRegPlateParserImplJudetTest() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate).getJudet()).isEqualTo(Judet.B);
    }

    @Test
    public void roRegPlateParserImplGetDigitsTest() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate).getDigits()).isEqualTo(new Short("101"));
    }

    @Test
    public void roRegPlateParserImplGetLettersTest() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate).getLetters()).isEqualTo("BBB");
    }

    @Test
    public void roRegPlateParserImplJudetTest0() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate0).getJudet()).isEqualTo(Judet.CJ);
    }

    @Test
    public void roRegPlateParserImplGetDigitsTest0() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate0).getDigits()).isEqualTo(new Short("01"));
    }

    @Test
    public void roRegPlateParserImplGetLettersTest0() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate0).getLetters()).isEqualTo("CJC");
    }

    @Test
    public void roRegPlateParserImplGetLettersTest1() {
        try {
            parser.parseRegistrationPlate(regPlate1).getJudet();
        } catch (final InvalidRoRegPlateException e) {
            assertThat(e).isNotEqualTo(null);
        }
    }

    @Test
    public void roRegPlateParserImplJudetTest1() throws Exception {
        final String regPlateTmp = " if 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.IF);
    }

    @Test
    public void roRegPlateParserImplJudetTest2() throws Exception {
        final String regPlateTmp = " TR01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.TR);
    }

    @Test
    public void roRegPlateParserImplJudetTest3() throws Exception {
        final String regPlateTmp = " Ab 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.AB);
    }

    @Test
    public void roRegPlateParserImplJudetTest4() throws Exception {
        assertThat(parser.parseRegistrationPlate(regPlate0).getJudet()).isEqualTo(Judet.CJ);
    }

    @Test
    public void roRegPlateParserImplJudetTest5() throws Exception {
        final String regPlateTmp = "t m 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.TM);
    }

    @Test
    public void roRegPlateParserImplJudetTest6() throws Exception {
        final String regPlateTmp = "Ar 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.AR);
    }

    @Test
    public void roRegPlateParserImplJudetTest7() throws Exception {
        final String regPlateTmp = "s j 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.SJ);
    }

    @Test
    public void roRegPlateParserImplJudetTest8() throws Exception {
        final String regPlateTmp = "OT 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.OT);
    }

    @Test
    public void roRegPlateParserImplJudetTest9() throws Exception {
        final String regPlateTmp = "h r 01 cjc";
        assertThat(parser.parseRegistrationPlate(regPlateTmp).getJudet()).isEqualTo(Judet.HR);
    }

}
