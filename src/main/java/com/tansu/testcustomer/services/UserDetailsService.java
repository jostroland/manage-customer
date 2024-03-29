package com.tansu.testcustomer.services;


import com.tansu.testcustomer.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserDetailsService implements UserDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	private final String username;
	private final String password;
	private final List<GrantedAuthority> authorities;

	public UserDetailsService(User userInfo) {
		this.username = userInfo.getEmail();
		this.password = userInfo.getPassword();
		this.authorities = Stream.of(userInfo.getRoles().split(","))
									.map(SimpleGrantedAuthority::new)
										.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
