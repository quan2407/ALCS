package com.example.coreservice.controller;

import com.example.coreservice.dto.request.AuthenticationRequest;
import com.example.coreservice.dto.request.RegisterRequest;
import com.example.coreservice.dto.response.AuthenticationResponse;
import com.example.coreservice.dto.response.ApiResponse;
import com.example.coreservice.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        var result = authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Đăng ký thành công!"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        var result = authenticationService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng nhập thành công!"));
    }
}