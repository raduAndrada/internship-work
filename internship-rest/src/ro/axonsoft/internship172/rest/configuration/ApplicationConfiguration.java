package ro.axonsoft.internship172.rest.configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
/**
 * Clasa de configurare pentru bean-urile de procesare
 * @author Andrada
 *
 */
@Configuration
public class ApplicationConfiguration {

	/**
	 * Bean cu proprietatiile de procesare cum ar fi fisere de input, output, data de referinta
	 * mod de functionare:fisier, baza de date id batch de procesare si dimensiunea unei pagini
	 * citite din  baza de date
	 * @return proprietatiile ca un singleton
	 */
    @Bean(name = "processingProperties")
    public VehicleOwnersProcessingProperties vehicleOwnersProcessingProperties() {
        return new VehicleOwnersProcessingProperties();
    }

    /**
     * Bean pentru transformarea datei din string in format java.util.date
     * @return data in format date
     */
    @Bean
    public Date referenceDate() {
        return new Date(LocalDate.parse(vehicleOwnersProcessingProperties().getReferenceDate()).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli());
    }

    @Bean
    public Multimap<String, String> judToSeriesMultimap() {
       final Map<String, List<String>> judToSeriesMap = vehicleOwnersProcessingProperties().getRoIdCardSeriesMap();
       final ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();
       judToSeriesMap.forEach((jud, series) -> builder.putAll(jud, series));
       return builder.build();
    }


}
