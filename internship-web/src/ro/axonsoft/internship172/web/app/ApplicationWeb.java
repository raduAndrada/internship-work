package ro.axonsoft.internship172.web.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationWeb {
	public static void main(final String args[]) {
		SpringApplication.run(ApplicationWeb.class, args);

	}

	// private StreamVehicleOwnersProcessor processor;
	// private DbVehicleOwnersProcessor dbProcessor;
	// private VehicleOwnersProcessingProperties processingProperties;

	// @Inject
	// public void setProcessor(final StreamVehicleOwnersProcessor processor) {
	// this.processor = processor;
	// }
	//
	// @Inject
	// public void setDbProcessor(final DbVehicleOwnersProcessor dbProcessor) {
	// this.dbProcessor = dbProcessor;
	// }
	//
	// @Inject
	// public void setProcessingProperties(final VehicleOwnersProcessingProperties
	// processingProperties) {
	// this.processingProperties = processingProperties;
	// }
	//
	// @PostConstruct
	// public void afterPropertiesSet() throws Exception {
	// if (processingProperties.getMode().equals("file")) {
	// try (FileInputStream inStream = new
	// FileInputStream(processingProperties.getInputFile())) {
	// try (FileOutputStream outStream = new
	// FileOutputStream(processingProperties.getOutputFile())) {
	// processor.process(inStream, outStream);
	// }
	// } catch (final FileNotFoundException intFileNotFoundEx) {
	// } catch (final IOException e) {
	//
	// }
	// }
	// else {
	// dbProcessor.process(1L);
	// }
	// }
}
