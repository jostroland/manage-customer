package com.tansu.testcustomer.config;


import com.tansu.testcustomer.services.UserServiceImpl;
import com.tansu.testcustomer.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Md4PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


	@Bean
	public UserDetailsService userDetailsService() {
		// public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		// UserDetails admin = User.withUsername("admin").password(encoder.encode("password")).roles("ADMIN").build();
		// UserDetails user = User.withUsername("user").password(encoder.encode("pwd")).roles("USER").build();
		// return new InMemoryUserDetailsManager(admin, user);
		return new UserServiceImpl();

	}


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(CsrfConfigurer::disable)
									.authorizeHttpRequests(
													 authorizeHttpRequests ->
															 authorizeHttpRequests.requestMatchers(
																	 "/api/v1/user",
																		 "/auth/**",
																		 "/api/access/**",
																		 "/h2-console/**",
																		 // resources for swagger to work properly
																		 "/v2/api-docs",
																		 "/v3/api-docs",
																		 "/v3/api-docs/**",
																		 "/swagger-resources",
																		 "/swagger-resources/**",
																		 "/configuration/ui",
																		 "/configuration/security",
																		 "/swagger-ui/**",
																		 "/webjars/**",
																		 "/swagger-ui.html"
																	 )
															 .permitAll())
													.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
													.requestMatchers("/api/v1/customer", "/api/v1/customer/*")
															.authenticated())
				.formLogin(Customizer.withDefaults()).build();

	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}




	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
}
