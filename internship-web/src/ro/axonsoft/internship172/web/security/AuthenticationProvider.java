package ro.axonsoft.internship172.web.security;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.web.util.RestUrlResolver;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProvider.class);

	private static final String USER_LOGIN_URI = "users/check-password";

	private RestUrlResolver restUrlResolver;

	private RestTemplate restTemplate;

	@Inject
	public void setRestTemplate(final RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Inject
	public void setRestUrlResolver(final RestUrlResolver restUrlResolver) {
		this.restUrlResolver = restUrlResolver;
	}

	@Override
	protected void additionalAuthenticationChecks(final UserDetails userDetails,
			final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

	}

	@Override
	protected UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		LOG.debug("Authentication request for {}", username);

		return new User(username, "******", ImmutableList.of(new SimpleGrantedAuthority("ADMIN")));

	}

}