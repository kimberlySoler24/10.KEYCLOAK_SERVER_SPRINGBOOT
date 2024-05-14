package com.api.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;


/*
 * Las anotaciones definidas en la parte de abajo se implementan especificamente en el ecosistema de Spring Security
   y la inyección de dependencias con lombok
*/ 
@Configuration //Indica que la clase contiene uno o más métodos anotados con @Bean
@EnableWebSecurity //Habilita la configuración de seguridad web en una aplicación
@EnableMethodSecurity // Permite aplicar reglas de seguridad a nivel de método en las clases de servicio o controladores utilizando anotaciones como @PreAuthorize, @PostAuthorize, @Secured, etc. 
@RequiredArgsConstructor //Lombok, una biblioteca que ayuda a reducir la cantidad de código boilerplate en Java
public class SecurityConfig {
	
	@Autowired
	private  JwtAuthenticationConverter jwtAuthenticationConverter;
	
	/*
	 * Este método configura la seguridad de una aplicación web Spring utilizando Spring Security. Deshabilita
	 * la protección CSRF, especifica que todas las solicitudes deben estar autenticada, configura la autenticación 
	 * JWT para el servidor de recursos OAuth2, establece una política de creación de sesiones sin estado y devuelve
	 * la cadena de filtros de seguridad configurada.
	 */
	
	
// Esta método permite configurar la seguridad en nuestra aplicación
	@Bean  //Un bean en Spring es simplemente un objeto que Spring administra, instancia y ensambla, y que está disponible para ser utilizado en la aplicación.
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { //este método configura la seguridad de una aplicación web Spring utilizando Spring Security. 
		return httpSecurity
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(http -> http.anyRequest().authenticated())
				.oauth2ResourceServer( oauth ->{
				 	oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter));
				})
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
}
