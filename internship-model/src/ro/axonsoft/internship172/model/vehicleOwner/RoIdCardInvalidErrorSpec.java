package ro.axonsoft.internship172.model.vehicleOwner;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.ErrorSpec;

public enum RoIdCardInvalidErrorSpec implements ErrorSpec<RoIdCardInvalidErrorSpec.Var> {
	RO_ID_CARD_INVALID;

	public enum Var implements ErrorSpec.Var {
		RO_ID_CARD_INVALID;

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
		return "vehicleOwners.create.ro-id-card-invalid";
	}

	@Override
	public List<Var> getVars() {
		return ImmutableList.of(RoIdCardInvalidErrorSpec.Var.RO_ID_CARD_INVALID);
	}
}
