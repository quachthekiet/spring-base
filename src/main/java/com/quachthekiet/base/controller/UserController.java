package com.quachthekiet.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quachthekiet.base.common.RestResponse;
import com.quachthekiet.base.model.User;
import com.quachthekiet.base.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PreAuthorize("hasAuthority('USER_READ')")
	@GetMapping("")
	public ResponseEntity<?> getUsers() {
		return ResponseEntity.ok(RestResponse.success(userService.getAllUsers()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
		if (id != user.getId()) {
			return ResponseEntity.badRequest().body("User ID mismatch");
		}
		userService.updateUser(user);
		return ResponseEntity.ok(user);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable int id) {
		User user = userService.getUserById(id);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}

	@GetMapping("/context")
	public ResponseEntity<?> getMethodName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return ResponseEntity.ok(authentication.getPrincipal());
	}

}
