package com.adbrassacoma.administrativo.infrastructure.config;

import com.adbrassacoma.administrativo.infrastructure.exception.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final UserDetailsService userDetailsService;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter, 
	                      @Lazy UserDetailsService userDetailsService,
	                      CustomAccessDeniedHandler customAccessDeniedHandler) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userDetailsService = userDetailsService;
		this.customAccessDeniedHandler = customAccessDeniedHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(session -> 
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers(
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/swagger-ui/index.html",
					"/v3/api-docs",
					"/v3/api-docs/**",
					"/swagger-resources/**",
					"/swagger-resources",
					"/webjars/**",
					"/api/auth/cadastro",
					"/api/auth/login",
					"/actuator/health",
					"/h2-console/**",
					"/error",
					"/favicon.ico"
				).permitAll()
				.requestMatchers("/api/auth/usuarios/**").hasRole("ADMIN")
				.requestMatchers("/api/permissoes/minhas").authenticated()
				.requestMatchers("/api/permissoes/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.userDetailsService(userDetailsService)
			.exceptionHandling(exceptions -> exceptions
				.accessDeniedHandler(customAccessDeniedHandler)
			)
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers(
				"/swagger-ui/**",
				"/swagger-ui.html",
				"/swagger-ui/index.html",
				"/v3/api-docs",
				"/v3/api-docs/**",
				"/swagger-resources/**",
				"/swagger-resources",
				"/webjars/**",
				"/favicon.ico"
			);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("*"));
		configuration.setAllowCredentials(false);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}

