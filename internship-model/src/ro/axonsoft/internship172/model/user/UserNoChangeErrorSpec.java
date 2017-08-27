package ro.axonsoft.internship172.model.user;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.ErrorSpec;

public enum UserNoChangeErrorSpec implements ErrorSpec<UserNoChangeErrorSpec.Var> {
	NO_CHANGE;

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
		return "users.update.no-change";
	}

	@Override
	public List<UserNoChangeErrorSpec.Var> getVars() {
		return ImmutableList.of(UserNoChangeErrorSpec.Var.USRNM);
	}
}
