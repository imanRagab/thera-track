package org.dci.theratrack.controller;

import jakarta.validation.Valid;
import java.util.Map;
import org.dci.theratrack.request.LoginRequest;
import org.dci.theratrack.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
          return ResponseEntity.ok().body(token);


        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());


        }
    }
}

