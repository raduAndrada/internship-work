package ro.axonsoft.internship172.rest.configuration;

import java.util.Date;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.axonsoft.internship172.business.impl.DbVehicleOwnersProcessorImpl;
import ro.axonsoft.internship172.business.impl.base.SystemClockTimeManager;
import ro.axonsoft.internship172.business.impl.base.TimeManager;
import ro.axonsoft.internship172.impl.RoIdCardParserImpl;
import ro.axonsoft.internship172.impl.RoIdCardSeriesJudMapperImpl;
import ro.axonsoft.internship172.impl.RoRegPlateParserImpl;
import ro.axonsoft.internship172.impl.VehicleOwnersProcessorImpl;
import ro.axonsoft.internship172.model.api.DbVehicleOwnersProcessor;
import ro.axonsoft.internship172.model.api.RoIdCardParser;
import ro.axonsoft.internship172.model.api.RoIdCardSeriesJudMapper;
import ro.axonsoft.internship172.model.api.RoRegPlateParser;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessor;
import ro.axonsoft.internship172.model.base.RoIdCardSeriesMapperUtil;

@Configuration
public class BusinessConfig {

	@Bean
	public TimeManager timeManager() {
		return new SystemClockTimeManager();
	}

	@Bean
	public RoIdCardSeriesJudMapper roIdCardSeriesJudMapper() {
		return new RoIdCardSeriesJudMapperImpl(RoIdCardSeriesMapperUtil.JUD_TO_SERIES_MAP);
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
		return new VehicleOwnersProcessorImpl(new Date());
	}

	@Bean
	public DbVehicleOwnersProcessor dbVehicleOwnersProcessor() {
		return new DbVehicleOwnersProcessorImpl(roRegPlateParser(), roIdCardParser(), vehicleOwnersProcessor());
	}
}
