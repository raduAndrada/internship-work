package ro.axonsoft.internship172.web.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import ro.axonsoft.internship172.web.security.AuthenticationProvider;
import ro.axonsoft.internship172.web.security.UserDetailsServiceImpl;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.exceptionHandling().and().authorizeRequests().antMatchers("/webjars/**").permitAll().antMatchers("/css/**")
				.permitAll().antMatchers("/js/**").permitAll().antMatchers("/login/**").permitAll()
				.antMatchers("/error/**").permitAll().antMatchers("/images/**").permitAll().antMatchers("/**")
				.authenticated().and().formLogin().loginPage("/login").permitAll().and().logout().and().rememberMe()
				.key("remember-me").rememberMeCookieName("remember-me").rememberMeParameter("remember-me")
				.tokenValiditySeconds(86400);
	}

	@Autowired
	public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		return new AuthenticationProvider();
	}

	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

}