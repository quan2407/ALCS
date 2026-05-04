package com.example.coreservice.controller;

import com.example.coreservice.dto.response.GoogleUserInfo;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.service.auth.AuthenticationService;
import com.example.coreservice.service.auth.GoogleOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleAuthController {
    private final GoogleOAuthService googleService;
    private final AuthenticationService authenticationService;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @GetMapping("/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException{
        String url = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=openid%20email%20profile";

        response.sendRedirect(url);
    }
    @GetMapping("/google/callback")
    public ResponseEntity<?> callback(@RequestParam String code) {

        GoogleUserInfo googleUser = googleService.getUserInfo(code);

        User user = authenticationService.handleGoogleLogin(googleUser);

        var authResponse = authenticationService.createAuthResponse(user);

        return ResponseEntity.ok(authResponse);
    }
}
