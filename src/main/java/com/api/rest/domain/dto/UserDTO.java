package com.api.rest.domain.dto;

import java.util.Set;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;


@Value
@RequiredArgsConstructor
@Builder
public class UserDTO {
	
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private String password;
	private Set<String> roles;

}
