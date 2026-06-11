package com.example.auth_service.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth_service.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String password = body.get("password");
        String token = authService.login(correo, password);

        if (token == null) {
            return ResponseEntity.status(401).body(Map.of(
                "error", "Credenciales inválidas"
            ));
        }

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
        String resultado = authService.register(correo, password);

        if ("El usuario ya existe".equals(resultado) || 
            "Correo inválido".equals(resultado)) {
            return ResponseEntity.status(400).body(Map.of(
            "error", resultado
    ));
}
        return ResponseEntity.ok(Map.of(
            "mensaje", resultado
        ));
    }
}