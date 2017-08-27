package ro.axonsoft.recrutare.rest.security;

import java.util.Optional;

import ro.axonsoft.internship172.business.impl.base.CurrentUserContext;

public class CurrentUserContextImpl implements CurrentUserContext {

	static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

	@Override
	public String getUsername() {
		return Optional.ofNullable(USERNAME.get()).orElseThrow(() -> new IllegalStateException("User not available"));
	}

}
