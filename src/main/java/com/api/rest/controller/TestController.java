package com.api.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Se crea en esta clase dos endpoints para probar

@RestController
public class TestController {
	
	@GetMapping("/hello-1")
	@PreAuthorize("hasRole('admin_client_role')")
	public String HelloAdmin() {
		return "Hello Spring Boot With Keycloak - ADMIN";
	}
	
	@GetMapping("/hello-2")
	@PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')") // user_client_role_ROLE
	public String HelloUser() {
		return "Hello Spring Boot With Keycloak - USER";
	}
}
