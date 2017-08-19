package ro.axonsoft.internship.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.easymock.EasyMockRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import ro.axonsoft.internship172.impl.VehicleOwnersAggregator;
import ro.axonsoft.internship172.model.api.ImtRoIdCardProperties;
import ro.axonsoft.internship172.model.api.ImtRoRegPlateProperties;
import ro.axonsoft.internship172.model.api.ImtVehicleOwnerRecord;
import ro.axonsoft.internship172.model.api.Judet;

@RunWith(EasyMockRunner.class)
public class VehicleOwnersAggregatorTest {

	@Rule
	public final ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private VehicleOwnersAggregator subject;

	@Before
	public void beforeEachTest() {
		subject = new VehicleOwnersAggregator(dt("2017-04-13"));
	}

	@Test
	public void empty() {
		assertThat(subject.getOddCount()).isEqualTo(0);
		assertThat(subject.getEvenCount()).isEqualTo(0);
		assertThat(subject.getPassedRegChangeDueDate()).isEqualTo(0);
		assertThat(subject.getUnregCarsCountByJud()).isEmpty();
	}

	@Test
	public void oneOddRegPlate() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("KX").number(112233).build())
				.idCardIssueDate(dt("2012-09-25"))
				.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.CJ).digits((short) 11).letters("ADC").build())
				.build());
		assertThat(subject.getOddCount()).isEqualTo(1);
		assertThat(subject.getEvenCount()).isEqualTo(0);
	}

	@Test
	public void oneEvenRegPlate() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("KX").number(112233).build())
				.idCardIssueDate(dt("2012-09-25"))
				.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.CJ).digits((short) 10).letters("ADC").build())
				.build());
		assertThat(subject.getOddCount()).isEqualTo(0);
		assertThat(subject.getEvenCount()).isEqualTo(1);
	}

	@Test
	public void onePassedOverdueRegPlate() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
				.idCardIssueDate(dt("2017-03-13"))
				.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.MM).digits((short) 11).letters("ADC").build())
				.build());
		assertThat(subject.getPassedRegChangeDueDate()).isEqualTo(1);
	}

	@Test
	public void oneUnregVehicle() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
				.idCardIssueDate(dt("2017-03-13")).build());
		assertThat(subject.getUnregCarsCountByJud()).isEqualTo(ImmutableMap.of(Judet.CJ.toString(), 1));
	}

	@Test
	public void complexFocusOddEven() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
				.idCardIssueDate(dt("2017-03-13"))
				.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.CJ).digits((short) 11).letters("ADC").build())
				.build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
						.idCardIssueDate(dt("2017-03-13"))
						.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.CJ).digits((short) 12).letters("ADC")
								.build())
						.build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
						.idCardIssueDate(dt("2017-03-13")).regPlate(ImtRoRegPlateProperties.builder().judet(Judet.CJ)
								.digits((short) 13).letters("ADC").build())
						.build());
		assertThat(subject.getOddCount()).isEqualTo(2);
		assertThat(subject.getEvenCount()).isEqualTo(1);
	}

	@Test
	public void complexFocusPassedOverdue() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
				.idCardIssueDate(dt("2017-03-13"))
				.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.MM).digits((short) 11).letters("ADC").build())
				.build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
						.idCardIssueDate(dt("2017-03-15"))
						.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.B).digits((short) 12).letters("ADC")
								.build())
						.build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
						.idCardIssueDate(dt("2017-02-10")).regPlate(ImtRoRegPlateProperties.builder().judet(Judet.MM)
								.digits((short) 13).letters("ADC").build())
						.build());
		assertThat(subject.getPassedRegChangeDueDate()).isEqualTo(2);
	}

	@Test
	public void complexFocusUnregVehicles() {
		subject.aggregate(ImtVehicleOwnerRecord.builder()
				.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
				.idCardIssueDate(dt("2017-03-13")).build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
						.idCardIssueDate(dt("2017-03-15"))
						.regPlate(ImtRoRegPlateProperties.builder().judet(Judet.CJ).digits((short) 12).letters("ADC")
								.build())
						.build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.CJ).series("CJ").number(112233).build())
						.idCardIssueDate(dt("2017-02-10")).build())
				.aggregate(ImtVehicleOwnerRecord.builder()
						.idCard(ImtRoIdCardProperties.builder().judet(Judet.MM).series("MM").number(112233).build())
						.idCardIssueDate(dt("2017-02-10")).build());
		assertThat(subject.getUnregCarsCountByJud()).isEqualTo(ImmutableMap.of("CJ", 2, "MM", 1));
	}

	private static final Date dt(final String strDt) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(strDt);
		} catch (final ParseException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}
