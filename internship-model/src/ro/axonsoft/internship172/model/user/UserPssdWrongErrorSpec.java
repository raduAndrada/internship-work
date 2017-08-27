package ro.axonsoft.internship172.model.user;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.error.ErrorSpec;

public enum UserPssdWrongErrorSpec implements ErrorSpec<UserPssdWrongErrorSpec.Var> {
	PSSD_WRONG;

	public enum Var implements ErrorSpec.Var {
		PSSD;

		@Override
		public Integer getIndex() {
			return 0;
		}

		@Override
		public String getName() {
			return "password";
		}
	}

	@Override
	public String getKey() {
		return "users.login.password-is-wrong";
	}

	@Override
	public List<UserPssdWrongErrorSpec.Var> getVars() {
		return ImmutableList.of(UserPssdWrongErrorSpec.Var.PSSD);
	}

}