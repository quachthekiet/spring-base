package com.quachthekiet.base.security.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.quachthekiet.base.model.User;
import com.quachthekiet.base.repository.UserRepository;
import com.quachthekiet.base.security.model.CustomUserDetails;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {



	private final UserRepository userRepository;
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if(user == null)
		{
			throw new UsernameNotFoundException("User with email " + email + " not found");
		}
		return CustomUserDetails.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(List.of(new SimpleGrantedAuthority(user.getRole().getName())))
				.build();
	}
}
