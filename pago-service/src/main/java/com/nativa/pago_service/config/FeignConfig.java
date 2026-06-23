package com.nativa.pago_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {
    
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            // Capturamos el contexto de la petición HTTP entrante
            ServletRequestAttributes attributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String token = request.getHeader(AUTHORIZATION_HEADER);
                
                // Si existe el token, lo propagamos a la petición saliente de Feign
                if (token != null) {
                    requestTemplate.header(AUTHORIZATION_HEADER, token);
                }
            }
        };
    }
}
