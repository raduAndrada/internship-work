package ro.axonsoft.internship172.web.security;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableList;

import ro.axonsoft.internship172.model.api.VehicleOwnerRecord;
import ro.axonsoft.internship172.web.util.RestUrlResolver;

public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProvider.class);

	private static final String USER_REMEMBER_ME_URI = "users/";

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
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final ResponseEntity<VehicleOwnerRecord> user = restTemplate
				.getForEntity(restUrlResolver.resolveRestUri(USER_REMEMBER_ME_URI, username), VehicleOwnerRecord.class);
		if (user.getStatusCode().equals(HttpStatus.OK)) {
			return new User(username, "****", ImmutableList.of(new SimpleGrantedAuthority("ADMIN")));
		} else {
			throw new UsernameNotFoundException("login.unkwown");
		}
	}

}