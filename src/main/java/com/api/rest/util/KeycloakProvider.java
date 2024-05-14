package com.api.rest.util;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;

public class KeycloakProvider {
	
	//se declaran las siguientes constantes estáticas llamadas poder construir la fuente realm y acceder a la API de Keycloak
	private static final String SERVER_URL = "http://localhost:9090";
	
	private static final String REALM_NAME = "spring-boot-realm-test";
	
	private static final String REALM_MASTER= "master";
	
	private static final String ADMIN_CLI= "admin-cli";
	
	private static final String USSER_CONSOLE= "orusAdmin";
	
	private static final String PASSWORD_CONSOLE= "orusAdmin";
	
	private static final String CLIENT_SECRET= "7cVXH7mBPpbPjzIbDyPojIinoQ6PRz61";
	
	//Metodo que permite acceder a la API de Keycloak
	public static RealmResource getRealmResource() {
		Keycloak keycloak = KeycloakBuilder.builder()
				.serverUrl(SERVER_URL)
				.realm(REALM_MASTER)
				.clientId(ADMIN_CLI)
				.username(USSER_CONSOLE)
				.password(PASSWORD_CONSOLE)
				.clientSecret(CLIENT_SECRET)
				.resteasyClient(new ResteasyClientBuilderImpl()
						.connectionPoolSize(10)
						.build())
				.build();
		return keycloak.realm(REALM_NAME);
	}
	
	
	public static UsersResource getUserResource() {
		RealmResource realmResource = getRealmResource();
		return realmResource.users();
	}
	
	
	
	
	
	
	
	
}
