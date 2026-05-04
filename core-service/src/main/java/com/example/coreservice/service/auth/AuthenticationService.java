package com.example.coreservice.service.auth;

import com.example.coreservice.dto.request.*;
import com.example.coreservice.dto.response.AuthenticationResponse;
import com.example.coreservice.dto.response.GoogleUserInfo;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.entity.auth.UserProfile;
import com.example.coreservice.enums.AuthProvider;
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
        return createAuthResponse(user);

    }
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String tokenType = jwtService.extractClaim(request.getRefreshToken(), claims -> claims.get("type", String.class));

        if (!"REFRESH".equals(tokenType)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (user.getRefreshExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return createAuthResponse(user);
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

        return createAuthResponse(user);
    }

    public void resendVerificationCode(ResendCodeRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isVerified()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }

        String newOtp = String.valueOf(100000 + new java.util.Random().nextInt(900000));
        user.setVerificationCode(newOtp);
        user.setVerificationExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), newOtp);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
    public AuthenticationResponse createAuthResponse(User user) {
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        user.setRefreshExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User handleGoogleLogin(GoogleUserInfo googleUser) {

        return userRepository.findByEmail(googleUser.getEmail())
                .map(existingUser -> handleExistingUser(existingUser, googleUser))
                .orElseGet(() -> createGoogleUser(googleUser));
    }

    private User createGoogleUser(GoogleUserInfo googleUser) {
        User user = User.builder()
                .email(googleUser.getEmail())
                .isVerified(true)
                .role(Role.USER)
                .provider(AuthProvider.GOOGLE)
                .providerId(googleUser.getSub())
                .build();

        UserProfile profile = UserProfile.builder()
                .firstName(extractFirstName(googleUser.getName()))
                .lastName(extractLastName(googleUser.getName()))
                .avatarUrl(googleUser.getPicture())
                .user(user)
                .build();

        user.setProfile(profile);

        return userRepository.save(user);
    }

    private String extractFirstName(String fullName) {
        if (fullName == null) return null;
        return fullName.split(" ")[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null) return null;
        String[] parts = fullName.split(" ");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }

    private User handleExistingUser(User user, GoogleUserInfo googleUser) {
        if (user.getProvider() == AuthProvider.LOCAL){
            throw new RuntimeException("Email already registered with password");
        }
        if (user.getProviderId() == null){
            user.setProviderId(googleUser.getSub());
        }
        return user;
    }
}
