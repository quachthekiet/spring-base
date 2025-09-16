package com.quachthekiet.base.security.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quachthekiet.base.model.User;
import com.quachthekiet.base.repository.UserRepository;
import com.quachthekiet.base.security.model.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User with email " + email + " not found");
		}

		List<GrantedAuthority> authorities = user.getRoles().stream()
				.flatMap(role -> {
					Stream<SimpleGrantedAuthority> roleAuth = Stream
							.of(new SimpleGrantedAuthority(role.getName()));

					Stream<SimpleGrantedAuthority> permAuth = role.getPermissions().stream()
							.map(perm -> new SimpleGrantedAuthority(perm.getName()));
					return Stream.concat(roleAuth, permAuth);
				})
				.collect(Collectors.toList());

		return CustomUserDetails.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(authorities)
				.build();
	}
}
