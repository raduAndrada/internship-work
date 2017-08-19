package ro.axonsoft.internship172.impl;

import java.util.Date;
import java.util.Iterator;

import ro.axonsoft.internship172.model.api.ImtVehicleOwnersMetrics;
import ro.axonsoft.internship172.model.api.VehicleOwnerRecord;
import ro.axonsoft.internship172.model.api.VehicleOwnersMetrics;
import ro.axonsoft.internship172.model.api.VehicleOwnersProcessor;

public class VehicleOwnersProcessorImpl implements VehicleOwnersProcessor {

	private final Date referenceDate;

	public VehicleOwnersProcessorImpl(final Date referenceDate) {
		this.referenceDate = referenceDate;
	}

	@Override
	public VehicleOwnersMetrics process(final Iterator<VehicleOwnerRecord> records) {
		final VehicleOwnersAggregator vehicleOwnersAggregator = new VehicleOwnersAggregator(referenceDate);

		while (records.hasNext()) {
			vehicleOwnersAggregator.aggregate(records.next());
		}

		// rezultatul final
		return ImtVehicleOwnersMetrics.builder().oddToEvenRatio(vehicleOwnersAggregator.getOddToEvenRatio())
				.unregCarsCountByJud(vehicleOwnersAggregator.getUnregCarsCountByJud())
				.passedRegChangeDueDate(vehicleOwnersAggregator.getPassedRegChangeDueDate()).build();

	}

}