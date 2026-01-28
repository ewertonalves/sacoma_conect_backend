package com.adbrassacoma.administrativo.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

	@Value("${jwt.secret:MinhaChaveSecretaSuperSeguraParaJWTTokenComPeloMenos256BitsDeTamanhoParaSeguranca}")
	private String secretKey;

	@Value("${jwt.expiration:86400000}") // 24 horas em milissegundos
	private Long expiration;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	}

	public String generateToken(String email) {
		log.debug("Gerando token JWT para email: {}", email);
		Map<String, Object> claims = new HashMap<>();
		String token = createToken(claims, email);
		log.debug("Token JWT gerado com sucesso para email: {}", email);
		return token;
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
			.claims(claims)
			.subject(subject)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiration))
			.signWith(getSigningKey())
			.compact();
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public Boolean validateToken(String token) {
		try {
			boolean isValid = !isTokenExpired(token);
			if (!isValid) {
				log.warn("Token JWT expirado ou inválido");
			}
			return isValid;
		} catch (Exception e) {
			log.error("Erro ao validar token JWT: {}", e.getMessage());
			return false;
		}
	}

	private SecretKey getSigningKey() {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}

