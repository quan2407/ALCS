package com.example.coreservice.service.auth;

import com.example.coreservice.dto.request.AuthenticationRequest;
import com.example.coreservice.dto.request.RegisterRequest;
import com.example.coreservice.dto.request.ResendCodeRequest;
import com.example.coreservice.dto.request.VerifyRequest;
import com.example.coreservice.dto.response.AuthenticationResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.enums.Role;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public void register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        String otpCode = String.valueOf(100000 + new java.util.Random().nextInt(900000));

        var user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isVerified(false)
                .verificationCode(otpCode)
                .verificationExpiry(LocalDateTime.now().plusMinutes(15))
                .build();
        userRepository.save(user);
        try {
            emailService.sendVerificationEmail(user.getEmail(),otpCode);
        } catch (Exception e) {
            System.err.println("Gửi mail thất bại: " + e.getMessage());
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.isVerified()) {
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public AuthenticationResponse verifyEmail(VerifyRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            throw new AppException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        if (user.getVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void resendVerificationCode(ResendCodeRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isVerified()) {
            throw new AppException(ErrorCode.INVALID_NOTE_CONTENT); // Tạm dùng hoặc tạo mã mới
        }

        String newOtp = String.valueOf(100000 + new java.util.Random().nextInt(900000));
        user.setVerificationCode(newOtp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), newOtp);
        } catch (Exception e) {
            // Lỗi hệ thống khi gửi mail
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
