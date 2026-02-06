package com.adbrassacoma.administrativo.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${rate.limit.general:100}")
    private int generalRateLimit;

    @Value("${rate.limit.auth:5}")
    private int authRateLimit;

    private final Map<String, Bucket> generalCache = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authCache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIP(request);
        String requestUri = request.getRequestURI();

        Bucket bucket;
        int limit;

        if (requestUri.startsWith("/api/auth/login") || requestUri.startsWith("/api/auth/cadastro")) {
            bucket = authCache.computeIfAbsent(clientIp, k -> createBucket(authRateLimit));
            limit = authRateLimit;
        } else if (requestUri.startsWith("/actuator") ||
                requestUri.startsWith("/swagger-ui") ||
                requestUri.startsWith("/v3/api-docs") ||
                requestUri.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        } else {
            bucket = generalCache.computeIfAbsent(clientIp, k -> createBucket(generalRateLimit));
            limit = generalRateLimit;
        }

        if (bucket.tryConsume(1)) {
            response.setHeader("X-Rate-Limit-Limit", String.valueOf(limit));
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));

            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, requestUri);

            response.setStatus(429);
            response.setHeader("X-Rate-Limit-Limit", String.valueOf(limit));
            response.setHeader("X-Rate-Limit-Remaining", "0");
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"timestamp\":\"" + java.time.LocalDateTime.now() + "\"," +
                            "\"status\":429," +
                            "\"error\":\"Too Many Requests\"," +
                            "\"message\":\"Você excedeu o limite de requisições. Tente novamente em 1 minuto.\"," +
                            "\"path\":\"" + requestUri + "\"}");
        }
    }

    private Bucket createBucket(int capacity) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
