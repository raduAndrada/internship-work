package ro.axonsoft.internship172.configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.base.MoreObjects;

/**
 * Clasa pentru extragerea proprietatiilor din application.yml
 *
 * @author Andrada
 *
 */
@ConfigurationProperties(prefix = "ro.axonsoft.internship172")
public class VehicleOwnersProcessingProperties {

    private String referenceDate;

    private File inputFile;
    private File outputFile;

    private String mode;

    private Map<String, List<String>> roIdCardSeriesMap;

    public String getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(final String referenceDate) {
        this.referenceDate = referenceDate;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(final File inputFile) {
        this.inputFile = inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public Map<String, List<String>> getRoIdCardSeriesMap() {
        return roIdCardSeriesMap;
    }

    public void setRoIdCardSeriesMap(final Map<String, List<String>> roIdCardSeriesMap) {
        this.roIdCardSeriesMap = roIdCardSeriesMap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("")
                .add("referenceDate", referenceDate)
                .add("inputFile", inputFile)
                .add("outputFile", outputFile)
                .add("roIdCardSeriesMap", roIdCardSeriesMap)
                .toString();
    }
}

