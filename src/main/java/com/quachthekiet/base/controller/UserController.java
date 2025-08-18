package com.quachthekiet.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quachthekiet.base.model.RestResponse;
import com.quachthekiet.base.model.User;
import com.quachthekiet.base.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	
	@GetMapping("")
	public ResponseEntity<?> getUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}
}
