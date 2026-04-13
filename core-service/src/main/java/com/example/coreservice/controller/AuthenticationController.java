package com.example.coreservice.controller;

import com.example.coreservice.dto.request.*;
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
    public ResponseEntity<ApiResponse<Void>> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Đăng ký thành công! Vui lòng kiểm tra email để lấy mã xác nhận."));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        var result = authenticationService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Đăng nhập thành công!"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        var result = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Làm mới token thành công!"));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verify(
            @RequestBody VerifyRequest request
    ) {
        // Sau khi verify thành công, Service sẽ trả về Token luôn
        var result = authenticationService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Xác thực email thành công!"));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<ApiResponse<Void>> resendCode(
            @RequestBody ResendCodeRequest request
    ) {
        authenticationService.resendVerificationCode(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Mã xác thực mới đã được gửi vào email của bạn!"));
    }
}