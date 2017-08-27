package ro.axonsoft.internship172.model.user;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.ErrorSpec;

public enum UsernmTakenErrorSpec implements ErrorSpec<UsernmTakenErrorSpec.Var> {
	USERNM_TAKEN;

	public enum Var implements ErrorSpec.Var {
		USRNM;

		@Override
		public Integer getIndex() {
			return 0;
		}

		@Override
		public String getName() {
			return "username";
		}
	}

	@Override
	public String getKey() {
		return "users.create.username-taken";
	}

	@Override
	public List<UsernmTakenErrorSpec.Var> getVars() {
		return ImmutableList.of(UsernmTakenErrorSpec.Var.USRNM);
	}

}