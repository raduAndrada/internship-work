package ro.axonsoft.internship172.model.vehicleOwner;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.ErrorSpec;

public enum RoIdCardTakenErrorSpec implements ErrorSpec<RoIdCardTakenErrorSpec.Var> {
	RO_ID_CARD_TAKEN;

	public enum Var implements ErrorSpec.Var {
		RO_ID_CARD;

		@Override
		public Integer getIndex() {
			return 0;
		}

		@Override
		public String getName() {
			return "ro-id-card";
		}
	}

	@Override
	public String getKey() {
		return "vehicleOwners.create.ro-id-card-taken";
	}

	@Override
	public List<Var> getVars() {
		return ImmutableList.of(RoIdCardTakenErrorSpec.Var.RO_ID_CARD);
	}
}
