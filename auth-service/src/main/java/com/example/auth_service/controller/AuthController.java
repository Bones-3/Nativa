package com.example.auth_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String password = body.get("password");
        log.info("Petición HTTP POST recibida en /auth/login - Autenticando correo: {}", correo);

        String token = authService.login(correo, password);

        if (token == null) {
            log.warn("Login rechazado para correo: {}", correo);
            return ResponseEntity.status(401).body(Map.of(
                "error", "Credenciales inválidas"
            ));
        }

        log.info("Login exitoso, token generado para correo: {}", correo);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Login exitoso",
            "token", token,
            "correo", correo
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String password = body.get("password");
        log.info("Petición HTTP POST recibida en /auth/register - Registrando correo: {}", correo);

        String resultado = authService.register(correo, password);

        if ("El usuario ya existe".equals(resultado) ||
            "Correo inválido".equals(resultado)) {
            log.warn("Registro rechazado para correo: {} - motivo: {}", correo, resultado);
            return ResponseEntity.status(400).body(Map.of(
            "error", resultado
    ));
}
        log.info("Registro exitoso para correo: {}", correo);
        return ResponseEntity.ok(Map.of(
            "mensaje", resultado
        ));
    }
}
