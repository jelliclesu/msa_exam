package com.sparta.msa_exam.auth;

import com.sparta.msa_exam.auth.core.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    @Value("${server.port}")
    private String serverPort;

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> singUp(@RequestBody User user) {
        User createdUser = authService.signUp(user);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(createdUser);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SignInRequest signInRequest){
        String token = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(new AuthResponse(token));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class AuthResponse {
        private String access_token;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class SignInRequest {
        private String username;
        private String password;
    }
}
