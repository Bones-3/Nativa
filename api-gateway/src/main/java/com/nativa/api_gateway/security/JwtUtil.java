package com.nativa.api_gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nativa.api_gateway.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
 
    @Value("${jwt.secret}")
    private String secret;
 
    // ── Clave HMAC-SHA derivada del secreto en application.yml ───────────────
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
 
    // ── jjwt 0.12.x: parser() · verifyWith() · parseSignedClaims() · getPayload()
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
 
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
 
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractAllClaims(token).get("roles");
    }
 
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
 
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token expirado");
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Token inválido o malformado");
        }
    }
}
