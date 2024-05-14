package com.api.rest.service.impl;

import java.util.Collections;
import java.util.List;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.api.rest.domain.dto.UserDTO;
import com.api.rest.service.IKeycloakService;
import com.api.rest.util.KeycloakProvider;

import io.micrometer.common.lang.NonNull;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService{

	//metodo para listar todos los usuarios en keycloak
	@Override
	public List<UserRepresentation> findAllUsers() {
		return KeycloakProvider.getRealmResource().users().list();
	}
	
	//metodo para buscar un usuario por el username
	@Override
	public List<UserRepresentation> searchUserByUsername(String username) {
		return KeycloakProvider.getRealmResource().users().searchByUsername(username, true);
	}
	
	//Metodo para crear un usuario nuevo en keycloak
	@Override
	public String createUser(@NonNull UserDTO userDTO) {
		int status = 0;
		UsersResource usersResource = KeycloakProvider.getUserResource();
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setFirstName(userDTO.getFirstName());
		userRepresentation.setLastName(userDTO.getLastName());
		userRepresentation.setEmail(userDTO.getEmail());
		userRepresentation.setUsername(userDTO.getUsername());
		userRepresentation.setEmailVerified(true);
		userRepresentation.setEnabled(true);
		
		Response response = usersResource.create(userRepresentation);
		status = response.getStatus();
		
		if (status == 201) {
			String path = response.getLocation().getPath();
			String userId = path.substring(path.lastIndexOf('/') + 1);
			
			CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
			credentialRepresentation.setTemporary(false);
			credentialRepresentation.setType(OAuth2Constants.PASSWORD);
			credentialRepresentation.setValue(userDTO.getPassword());
			
			usersResource.get(userId).resetPassword(credentialRepresentation);
			
			RealmResource realmResource = KeycloakProvider.getRealmResource();
			
			List<RoleRepresentation> roleRepresentations = null;
			
			if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
				roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());
			} else {
				roleRepresentations = realmResource.roles()
						.list()
						.stream()
						.filter(role -> userDTO.getRoles()
						.stream()
						.anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
						.toList();
			}
			
			realmResource.users()
			.get(userId).roles()
			.realmLevel()
			.add(roleRepresentations);
			
			return "User created successfully";
		} else if (status == 409) {
			log.error("User exist already!");
			return "User exist already!";
		} else {
			log.error("Error creating user, please contact with the administrator!!");
			return "Error creating user, please contact with the administrator!!";
		}
	}

	//Metodo para borrar un usuario en keycloak
	@Override
	public void deleteUser(String userId) {
		KeycloakProvider
		.getUserResource()
		.get(userId)
		.remove();;
	}

	//Metodo para actualizar un usuario en keycloak
	@Override
	public void updateUser(String userId, @NonNull UserDTO userDTO) {
		
		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
		credentialRepresentation.setTemporary(false);
		credentialRepresentation.setType(OAuth2Constants.PASSWORD);
		credentialRepresentation.setValue(userDTO.getPassword());
		
		UsersResource usersResource = KeycloakProvider.getUserResource();
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setFirstName(userDTO.getFirstName());
		userRepresentation.setLastName(userDTO.getLastName());
		userRepresentation.setEmail(userDTO.getEmail());
		userRepresentation.setUsername(userDTO.getUsername());
		userRepresentation.setEmailVerified(true);
		userRepresentation.setEnabled(true);
		userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
		
		
		UserResource userResource = KeycloakProvider.getUserResource().get(userId);
		userResource.update(userRepresentation);
	}
	
	

}
