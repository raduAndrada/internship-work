package ro.axonsoft.internship172.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.junit.Test;

import ro.axonsoft.internship172.model.api.ImtVehicleOwnerParseError;
import ro.axonsoft.internship172.model.api.ImtVehicleOwnersMetrics;
import ro.axonsoft.internship172.model.api.ImtVehicleOwnersProcessResult;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessResult;

public class ApplicationTests {

	private static final String DFT_JCIS_URL = "file:jcis.yml";

	private static final String JCIS_URL_SYSTEM_PROP = "ro.axonsoft.internship.jcis.url";

	@Test
	public void test() throws Exception {
		final String tempDir = System.getProperty("java.io.tmpdir");
		final File outputFile = new File(tempDir + "/test-1-output.csv");
		if (outputFile.exists()) {
			outputFile.delete();
		}
		System.setProperty(JCIS_URL_SYSTEM_PROP, DFT_JCIS_URL);
		Application.main(new String[] { "input.csv", outputFile.getAbsolutePath() });
		try (final ObjectInputStream resIn = new ObjectInputStream(new FileInputStream(outputFile))) {
			final Object resultObj = resIn.readObject();
			assertThat(resultObj).isInstanceOf(VehicleOwnersProcessResult.class);
			final VehicleOwnersProcessResult result = (VehicleOwnersProcessResult) resultObj;
			assertThat(result).isEqualTo(ImtVehicleOwnersProcessResult.builder()
					.metrics(ImtVehicleOwnersMetrics.builder().oddToEvenRatio(200).putUnregCarsCountByJud("MM", 3)
							.passedRegChangeDueDate(0).build())
					.addErrors(ImtVehicleOwnerParseError.builder().line(1).type(0).build()).build());
		}
		if (outputFile.exists()) {
			outputFile.delete();
		}
	}
}
