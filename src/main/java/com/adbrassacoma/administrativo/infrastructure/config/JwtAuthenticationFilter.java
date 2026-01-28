package com.adbrassacoma.administrativo.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		String method = request.getMethod();
		String path = requestPath.toLowerCase();
		
		if ("OPTIONS".equalsIgnoreCase(method)) {
			return true;
		}
		
		return path.startsWith("/swagger-ui") || 
			   path.startsWith("/v3/api-docs") ||
			   path.startsWith("/swagger-resources") ||
			   path.startsWith("/webjars") ||
			   path.startsWith("/api/auth/cadastro") ||
			   path.startsWith("/api/auth/login") ||
			   path.startsWith("/h2-console") ||
			   path.startsWith("/actuator") ||
			   path.equals("/swagger-ui.html") ||
			   path.equals("/favicon.ico");
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			jwt = authHeader.substring(7);
			
			if (!jwtService.validateToken(jwt)) {
				filterChain.doFilter(request, response);
				return;
			}
			
			userEmail = jwtService.extractUsername(jwt);

			if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

				if (jwtService.validateToken(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
					);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Token JWT inválido ou expirado\"}");
			return;
		}

		filterChain.doFilter(request, response);
	}
}

