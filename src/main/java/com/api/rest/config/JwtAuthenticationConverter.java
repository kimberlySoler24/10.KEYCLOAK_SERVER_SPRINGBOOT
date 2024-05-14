package com.api.rest.config;


import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken>{
	
	/*
	 *  Esta instancia parece ser utilizada para convertir las autoridades (roles) extraídas de un token 
	 *  JWT en objetos GrantedAuthority, que son utilizados por Spring Security para la autorización.
	 */
	
	private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter(); //permisos que va a tener nuestros usuario

	
	//Se inyectan valores definidos en el archivo application.properties
	
	@Value("${jwt.auth.converter.principle-attribute}")
	private String principalAttribute;
	 
	@Value("${jwt.auth.converter.resource-id}")
	private String resourceId;
	
	
	//Metodo creado para convertir un objeto JWT en un objeto de autenticación de Spring Security
	@Override //prioriza este metodo sobre los demás que se llamen del mismo modo
	public AbstractAuthenticationToken convert(Jwt jwt) {
		
		//el stream.concat sirve para unir dos flujos en uno solo
		Collection<GrantedAuthority> authorities = Stream
				.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), extractResourceRoles(jwt).stream())
				.toList();
		
		return new JwtAuthenticationToken(jwt, authorities, getPrincipleName(jwt));
	}
	
	
	/*
	 * Esta función verifica si un token JWT  contiene información sobre los accesos a recursos y,
	 * si hay, extrae los roles asociados a un recurso especifico, los transforma en autoridades concedidas 
	 * y los devuelve como una colección
	 * 
	 * "resource_access": {
    "spring-client-api-rest-test": {
      "roles": [
        "user_client_role"
      ]
    },
	 */
	
	private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt){
		Map<String, Object> resourceAccess; //contendrá información sobre los accesos a los recursos
		Map<String, Object> resource; //contendrá información específica sobre un recurso en particular
		Collection<String> resourceRoles; //almacenará los roles asociados a un recurso en particular
		
		if (jwt.getClaim("resource_access") == null) {
			return Set.of(); //sino contiene información devuelve un conjunto vacio de autoridades concedidas
		}
		
		resourceAccess = jwt.getClaim("resource_access"); //se obtiene el mapa resourceAccess del token JWT
		
		if (resourceAccess.get(resourceId) == null) {
			return Set.of(); //sino hay información para el recurso identificado por "resourceId", 
			//devuelve un conjunto vacio de autoridades concedidas
		}
		
		//al haber información sobre el recurso en cuestion, se obtiene el mapa "resource" correspondiente a ese recurso
		resource = (Map<String, Object>) resourceAccess.get(resourceId);
		
		//verifica si hay roles asociados al recurso en el mapa "resource". al no haber, devuelve
		//un conjunto vacío de autoridades concedidas
		if (resource.get("roles") == null) {
			return Set.of();
		}
		
		//se obtiene la colección de roles del mapa resource
		resourceRoles = (Collection<String>) resource.get("roles"); // 
		
		//se mapean los roles obtenidos a instancias de "SimpleGrantedAuthority", que son autoridades concedidas
		//en srping security, donde los nombre de roles se les antepone el prefijo "ROLE_" y se devuelve como una lista
		return resourceRoles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_".concat(role))) //ROLE_user
				.toList();
	}
	
	/*
	 * Este método se encarga de obtener el nombre del principal (usuario) asociado al token JWT 
	 * proporcionado como argumento.
	 */
	
	private String getPrincipleName(Jwt jwt) {
		String claimName = JwtClaimNames.SUB; //Indica el nombre de la reclamación del token JWT que generalmente contiene el identificador unico del sujeto
		if (principalAttribute != null) {
			claimName = principalAttribute; //si es distinto de null la variable tomará el valor principalAttribute
		}
		
		return jwt.getClaim(claimName); //obtiene el ombre del usuario principal asocialdo al token JWT
	}
	
	
	
}
