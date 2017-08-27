package ro.axonsoft.internship172.model.user;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.ErrorSpec;

public enum UsernameDoesNotExistErrorSpec implements ErrorSpec<UsernameDoesNotExistErrorSpec.Var> {
	USERNM_DOES_NOT_EXIST;

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
		return "users.login.username-does-not-exist";
	}

	@Override
	public List<UsernameDoesNotExistErrorSpec.Var> getVars() {
		return ImmutableList.of(UsernameDoesNotExistErrorSpec.Var.USRNM);
	}

}
