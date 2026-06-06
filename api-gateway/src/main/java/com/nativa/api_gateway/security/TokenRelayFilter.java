package com.nativa.api_gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TokenRelayFilter implements GatewayFilter{
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
 
        // Sin token → continuar sin modificar (rutas públicas)
        if (authHeader == null) {
            return chain.filter(exchange);
        }
 
        log.debug("Relaying Authorization header to downstream service");
 
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();
 
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
