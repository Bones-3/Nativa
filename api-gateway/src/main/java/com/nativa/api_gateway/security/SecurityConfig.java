package com.nativa.api_gateway.security;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
 
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // ── API REST stateless: deshabilitar sesiones ────────────────────────
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
 
                // ── Respuestas de error en JSON (no redirect de Spring Security) ──────
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> {
                            var resp = exchange.getResponse();
                            resp.setStatusCode(HttpStatus.UNAUTHORIZED);
                            resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            var buf = resp.bufferFactory().wrap(
                                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Autenticaci\u00f3n requerida\"}"
                                            .getBytes());
                            return resp.writeWith(Mono.just(buf));
                        })
                        .accessDeniedHandler((exchange, e) -> {
                            var resp = exchange.getResponse();
                            resp.setStatusCode(HttpStatus.FORBIDDEN);
                            resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            var buf = resp.bufferFactory().wrap(
                                    "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Sin permiso para este recurso\"}"
                                            .getBytes());
                            return resp.writeWith(Mono.just(buf));
                        })
                )
 
                // ── Reglas de acceso ─────────────────────────────────────────────────
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .pathMatchers("/actuator/health").permitAll()
                        .anyExchange().authenticated()
                )
 
                // ── Filtro JWT antes de la capa de autenticación de Spring Security ──
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
