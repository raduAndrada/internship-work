package ro.axonsoft.internship172.configuration;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Multimap;

import ro.axonsoft.internship172.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.api.RoIdCardParser;
import ro.axonsoft.internship172.api.RoIdCardSeriesJudMapper;
import ro.axonsoft.internship172.api.RoRegPlateParser;
import ro.axonsoft.internship172.api.StreamVehicleOwnersProcessor;
import ro.axonsoft.internship172.api.VehicleOwnersProcessor;
import ro.axonsoft.internship172.data.DbVehicleOwnersProcessorImpl;
import ro.axonsoft.internship172.data.impl.ResultServiceImpl;
import ro.axonsoft.internship172.data.impl.VehicleOwnerServiceImpl;
import ro.axonsoft.internship172.data.services.ResultService;
import ro.axonsoft.internship172.data.services.VehicleOwnerService;
import ro.axonsoft.internship172.impl.RoIdCardParserImpl;
import ro.axonsoft.internship172.impl.RoIdCardSeriesJudMapperImpl;
import ro.axonsoft.internship172.impl.RoRegPlateParserImpl;
import ro.axonsoft.internship172.impl.StreamVehicleOwnersProcessorImpl;
import ro.axonsoft.internship172.impl.VehicleOwnersProcessorImpl;

/**
 * Clasa de configurare pentru nivelul de business al aplicatiei
 *
 * @author Andrada
 *
 */
@Configuration
public class BusinessConfiguration {

    /**
     * Se injecteaza data de referinta si se creaza bean-urile care vor fi
     * folosite la procesare
     */
    private Date referenceDate;
    private Multimap<String, String> judToSeriesMultimap;

    @Inject
    public void setReferenceDate(@Named("referenceDate") final Date referenceDate) {
        this.referenceDate = referenceDate;
    }

    @Inject
    public void setJudToSeriesMultimap(@Named("judToSeriesMultimap") final Multimap<String, String> judToSeriesMultimap) {
        this.judToSeriesMultimap = judToSeriesMultimap;
    }

    @Bean
    public RoIdCardSeriesJudMapper roIdCardSeriesJudMapper() {
        return new RoIdCardSeriesJudMapperImpl(judToSeriesMultimap);
    }

    @Bean
    public RoIdCardParser roIdCardParser() {
        return new RoIdCardParserImpl(roIdCardSeriesJudMapper());
    }

    @Bean
    public RoRegPlateParser roRegPlateParser() {
        return new RoRegPlateParserImpl();
    }

    @Bean
    public VehicleOwnersProcessor vehicleOwnersProcessor() {
        return new VehicleOwnersProcessorImpl(referenceDate);
    }

    @Bean
    public StreamVehicleOwnersProcessor streamVehicleOwnersProcessor() {
        return new StreamVehicleOwnersProcessorImpl(
                roRegPlateParser(),
                roIdCardParser(),
                vehicleOwnersProcessor());
    }
    @Bean
    public VehicleOwnerService vehicleOwnerService() {
        return new VehicleOwnerServiceImpl();
    }

    @Bean
    public ResultService resultService() {
        return new ResultServiceImpl();
    }

    @Bean
    public DbVehicleOwnersProcessor dbVehicleOwnersProcessor() {
        return new DbVehicleOwnersProcessorImpl(
                roRegPlateParser(),
                roIdCardParser(),
                vehicleOwnersProcessor());
    }



}