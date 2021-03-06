package ro.axonsoft.internship172.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.axonfost.internship172.business.api.result.ResultBusiness;
import ro.axonfost.internship172.business.impl.result.ResultBusinessImpl;
import ro.axonsoft.internship172.business.api.user.UserBusiness;
import ro.axonsoft.internship172.business.api.vehicleOwner.VehicleOwnerBusiness;
import ro.axonsoft.internship172.business.impl.user.UserBusinessImpl;
import ro.axonsoft.internship172.business.impl.vehicleOwner.VehicleOwnerBusinessImpl;

@Configuration
public class BusinessConfigurationForRest {

	@Bean
	public VehicleOwnerBusiness vehicleOwnerBusiness() {
		return new VehicleOwnerBusinessImpl();
	}

	@Bean
	public ResultBusiness resultBusiness() {
		return new ResultBusinessImpl();
	}

	@Bean
	public UserBusiness userBusiness() {
		return new UserBusinessImpl();
	}
}
