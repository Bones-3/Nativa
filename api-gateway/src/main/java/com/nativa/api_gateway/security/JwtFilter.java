package com.nativa.api_gateway.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.nativa.api_gateway.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter{
   private static final String BEARER_PREFIX = "Bearer ";
 
    private final JwtUtil jwtUtil;
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
 
        // Sin token → continuar. Rutas públicas pasan; protegidas las bloquea SecurityConfig.
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }
 
        String token = authHeader.substring(BEARER_PREFIX.length());
 
        try {
            jwtUtil.validateToken(token);
        } catch (UnauthorizedException e) {
            log.warn("Acceso denegado en gateway: {}", e.getMessage());
            return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
 
        UsernamePasswordAuthenticationToken authentication = buildAuthentication(token);
        log.debug("Token válido para usuario: {}", authentication.getName());
 
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
 
    // ── Construye el objeto Authentication con username y roles del token ─────
    private UsernamePasswordAuthenticationToken buildAuthentication(String token) {
        String username = jwtUtil.extractUsername(token);
 
        List<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
 
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
 
    // ── Escribe la respuesta de error directamente sin pasar al microservicio ─
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
 
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message
        );
 
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
