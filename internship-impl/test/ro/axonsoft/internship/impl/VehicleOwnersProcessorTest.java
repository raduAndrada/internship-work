package ro.axonsoft.internship.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import ro.axonsoft.internship172.api.ImtVehicleOwnerParseError;
import ro.axonsoft.internship172.api.StreamVehicleOwnersProcessor;
import ro.axonsoft.internship172.api.VehicleOwnerParseError;
import ro.axonsoft.internship172.api.VehicleOwnersProcessResult;
import ro.axonsoft.internship172.impl.RoIdCardParserImpl;
import ro.axonsoft.internship172.impl.RoIdCardSeriesJudMapperImpl;
import ro.axonsoft.internship172.impl.RoRegPlateParserImpl;
import ro.axonsoft.internship172.impl.StreamVehicleOwnersProcessorImpl;
import ro.axonsoft.internship172.impl.VehicleOwnersProcessorImpl;

public class VehicleOwnersProcessorTest {

    private static final String DFT_JCIS_URL = "file:jcis.yml";

    private static final String JCIS_URL_SYSTEM_PROP = "ro.axonsoft.internship.jcis.url";

    private static final Date REF_DATE = Date.from(LocalDate.parse("2017-04-13").atStartOfDay(ZoneId.systemDefault()).toInstant());

    private final StreamVehicleOwnersProcessor subject = new StreamVehicleOwnersProcessorImpl(
            new RoRegPlateParserImpl(),
            new RoIdCardParserImpl(new RoIdCardSeriesJudMapperImpl(RoIdCardSeriesMapperUtil.JUD_TO_SERIES_MAP)),
            new VehicleOwnersProcessorImpl(REF_DATE));



    public VehicleOwnersProcessorTest() {
        System.setProperty(JCIS_URL_SYSTEM_PROP, DFT_JCIS_URL);
    }

    /**
     * Fișierul de intrare este gol.
     */
    @Test
    public void emptyInput() throws Exception {
        final ResultAdapter result = runProcess("empty");
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getOddToEvenRatio()).isEqualTo(0);
        assertThat(result.getPassedRegChangeDueDate()).isEqualTo(0);
        assertThat(result.getUnregCarsCountByJud()).isNullOrEmpty();
    }

    /**
     * MM636141;2012-09-25;MM84ADC;
     */
    @Test
    public void oneCorrectLine() throws Exception {
        final ResultAdapter result = runProcess("oneCorrectLine");
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getPassedRegChangeDueDate()).isEqualTo(0);
        assertThat(result.getUnregCarsCountByJud()).isNullOrEmpty();
        assertThat(result.getOddToEvenRatio()).isEqualTo(0);
    }

    /**
     * CJ636141;2012-09-25;CJ84ADC;
     * CJ636141;2012-09-25;CJ81ADC;
     * CJ636141;2012-09-25;CJ85ADC;
     */
    @Test
    public void correctLinesFocusOddEven() throws Exception {
        final ResultAdapter result = runProcess("correctLinesFocusOddEven");
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getOddToEvenRatio()).isEqualTo(200);
    }

    /**
     * CJ636141;2012-03-13;MM84ADC;
     * CJ636141;2012-03-14;MM81ADC;
     * CJ636141;2012-03-15;MM85ADC;
     * MM636141;2012-03-13;CJ84ADC;
     * MM636141;2012-03-14;CJ81ADC;
     * MM636141;2012-03-15;CJ85ADC;
     */
    @Test
    public void correctFocusPassedOverdue() throws Exception {
        final ResultAdapter result = runProcess("correctFocusPassedOverdue");
        assertThat(result.getErrors()).isEmpty();
        //cred ca aici ar trebui sa fie 6
        assertThat(result.getPassedRegChangeDueDate()).isEqualTo(6);
    }

    /**
     * CJ636141;2012-09-25;XCJ84ADC;
     * CJ636141;2012-09-25;;
     * CJ636141;2012-09-25;CJ85ADC;
     * MM636141;2012-09-25;XCJ84ADC;
     * MM636141;2012-09-25;CJ85ADC;
     */
    @Test
    public void correctFocusUnregVehicles() throws Exception {
        final ResultAdapter result = runProcess("correctFocusUnregVehicles");
        assertThat(result.getErrors()).isEmpty();
        assertThat(result.getUnregCarsCountByJud()).isEqualTo(ImmutableMap.of(
                "CJ", 2,
                "MM", 1
                ));
    }


    /**
     * Fișierul conține spații în diferite locuri permise și linii goale.
     */
    @Test
    public void correctFocusIgnoreSpaces() throws Exception {
        final ResultAdapter result = runProcess("correctFocusIgnoreSpaces");
        assertThat(result.getErrors()).isEmpty();
    }

    /**
     * Fișierul conține cărți de indentitate și numere de înmatriculare cu litere mici.
     */
    @Test
    public void correctFocusIgnoreCase() throws Exception {
        final ResultAdapter result = runProcess("correctFocusIgnoreCase");
        assertThat(result.getErrors()).isEmpty();
    }

    /**
     * Fișierul conține linii invalide.
     */
    @Test
    public void errorsInvalidLines() throws Exception {
        final ResultAdapter result = runProcess("errorsInvalidLines");
        assertThat(result.getErrors()).isEqualTo(ImmutableSet.of(
                ImtVehicleOwnerParseError.builder().line(1).type(0).build(),
                ImtVehicleOwnerParseError.builder().line(3).type(0).build()
                ));
    }

    /**
     * Fișierul conține linii cu carți de identitate invalide.
     */
    @Test
    public void errorsInvalidIdCards() throws Exception {
        final ResultAdapter result = runProcess("errorsInvalidIdCards");
        assertThat(result.getErrors()).isEqualTo(ImmutableSet.of(
                ImtVehicleOwnerParseError.builder().line(1).type(1).build(),
                ImtVehicleOwnerParseError.builder().line(3).type(1).build()
                ));
    }

    /**
     * Fișierul conține linii cu date de emitere cărți de identitate invalide.
     */
    @Test
    public void errorsInvalidIdCardIssueDates() throws Exception {
        final ResultAdapter result = runProcess("errorsInvalidIdCardIssueDates");
        assertThat(result.getErrors()).isEqualTo(ImmutableSet.of(
                ImtVehicleOwnerParseError.builder().line(1).type(2).build(),
                ImtVehicleOwnerParseError.builder().line(2).type(2).build()
                ));
    }

    /**
     * Fișierul conține linii unele erori. Se evaluează și rezultatul pe liniile corecte.
     */
    @Test
    public void complex() throws Exception {
        final ResultAdapter result = runProcess("complex");
        assertThat(result.getErrors()).isEqualTo(ImmutableSet.of(
                ImtVehicleOwnerParseError.builder().line(4).type(2).build(),
                ImtVehicleOwnerParseError.builder().line(7).type(1).build(),
                ImtVehicleOwnerParseError.builder().line(7).type(2).build()
                ));
        assertThat(result.getUnregCarsCountByJud()).isEqualTo(ImmutableMap.of(
                "B", 2,
                "CJ", 1,
                "MM", 1
                ));
        assertThat(result.getPassedRegChangeDueDate()).isEqualTo(2);
        assertThat(result.getOddToEvenRatio()).isEqualTo(66);
    }

    private ResultAdapter runProcess(final String inputFileName) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        subject.process(getClass().getClassLoader().getResourceAsStream("samples/" + inputFileName + ".csv"), bout);
        final ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
        return new ResultAdapter((VehicleOwnersProcessResult) oin.readObject());
    }

    private static class ResultAdapter {
        private final VehicleOwnersProcessResult result;

        public ResultAdapter(final VehicleOwnersProcessResult result) {
            this.result = result;
        }

        public Integer getPassedRegChangeDueDate() {
            return result.getMetrics().getPassedRegChangeDueDate();
        }

        public Map<String, Integer> getUnregCarsCountByJud() {
            return result.getMetrics().getUnregCarsCountByJud();
        }

        public Integer getOddToEvenRatio() {
            return result.getMetrics().getOddToEvenRatio();
        }

        public Set<VehicleOwnerParseError> getErrors() {
            return Optional.fromNullable(result.getErrors()).or(ImmutableSet.of()).stream()
                    .map(ImtVehicleOwnerParseError::copyOf)
                    .collect(Collectors.toCollection(Sets::newLinkedHashSet));
        }
    }
}
