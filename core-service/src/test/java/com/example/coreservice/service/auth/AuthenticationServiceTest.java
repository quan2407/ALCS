package com.example.coreservice.service.auth;

import com.example.coreservice.dto.request.AuthenticationRequest;
import com.example.coreservice.dto.request.RefreshTokenRequest;
import com.example.coreservice.dto.request.RegisterRequest;
import com.example.coreservice.dto.request.VerifyRequest;
import com.example.coreservice.dto.response.AuthenticationResponse;
import com.example.coreservice.entity.auth.User;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.repository.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        assertDoesNotThrow(() -> authenticationService.register(request));

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void register_EmailExisted_ThrowException() {
        // 1. Mock dữ liệu
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existed@gmail.com");

        when(userRepository.existsByEmail("existed@gmail.com")).thenReturn(true);

        // 2. Thực thi và kiểm tra lỗi
        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.register(request);
        });

        // 3. Kiểm tra mã lỗi trả về có đúng 1005 (USER_EXISTED) không
        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());

        // Đảm bảo không lưu user nào vào DB
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailServiceFails_UserStillSaved() {
        // Giả lập: Lưu DB thành công nhưng gửi mail bị ném Exception
        RegisterRequest request = new RegisterRequest();
        request.setEmail("failmail@gmail.com");
        request.setPassword("password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        // Giả lập lỗi gửi mail
        doThrow(new RuntimeException("Mail server down"))
                .when(emailService).sendVerificationEmail(anyString(), anyString());

        // Thực thi
        assertDoesNotThrow(() -> authenticationService.register(request));

        // Kiểm chứng: User vẫn phải được lưu dù gửi mail lỗi (theo logic hiện tại của Quân)
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void register_VerifyDataMapping() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("mapping@gmail.com");
        request.setPassword("raw_password");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_pass");

        authenticationService.register(request);

        // Dùng ArgumentCaptor để "tóm" lấy đối tượng User trước khi save vào DB
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        // Kiểm tra xem các trường mapping có đúng không
        assertEquals("mapping@gmail.com", savedUser.getEmail());
        assertEquals("encoded_pass", savedUser.getPasswordHash());
        assertFalse(savedUser.isVerified()); // Mặc định phải là false
        assertNotNull(savedUser.getVerificationCode());
        assertEquals(6, savedUser.getVerificationCode().length()); // OTP phải 6 số
    }
    @Test
    void verifyEmail_Success() {
        // 1. Giả lập dữ liệu: User tồn tại và có mã khớp, chưa hết hạn
        VerifyRequest request = new VerifyRequest();
        request.setEmail("quan@gmail.com");
        request.setCode("123456");

        User user = User.builder()
                .email("quan@gmail.com")
                .verificationCode("123456")
                .verificationExpiry(LocalDateTime.now().plusMinutes(5)) // Còn hạn
                .isVerified(false)
                .build();

        when(userRepository.findByEmail("quan@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");

        // 2. Thực thi
        AuthenticationResponse response = authenticationService.verifyEmail(request);

        // 3. Kiểm chứng
        assertTrue(user.isVerified()); // Phải chuyển thành true
        assertNull(user.getVerificationCode()); // Phải xóa mã cũ
        assertNotNull(response.getAccessToken());
        verify(userRepository, times(2)).save(user); // 1 lần ở verify, 1 lần ở createAuthResponse
    }

    @Test
    void verifyEmail_WrongCode_ThrowException() {
        // Giả lập mã trong DB là "123456" nhưng gửi lên "654321"
        VerifyRequest request = new VerifyRequest();
        request.setEmail("quan@gmail.com");
        request.setCode("654321");

        User user = User.builder()
                .email("quan@gmail.com")
                .verificationCode("123456")
                .build();

        when(userRepository.findByEmail("quan@gmail.com")).thenReturn(Optional.of(user));

        // Kiểm tra lỗi 1006 (INVALID_VERIFICATION_CODE)
        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.verifyEmail(request);
        });

        assertEquals(ErrorCode.INVALID_VERIFICATION_CODE, exception.getErrorCode());
    }

    @Test
    void verifyEmail_CodeExpired_ThrowException() {
        // Giả lập mã đúng nhưng thời gian đã trôi qua (hết hạn)
        VerifyRequest request = new VerifyRequest();
        request.setEmail("quan@gmail.com");
        request.setCode("123456");

        User user = User.builder()
                .email("quan@gmail.com")
                .verificationCode("123456")
                .verificationExpiry(LocalDateTime.now().minusMinutes(1)) // Đã hết hạn 1 phút trước
                .build();

        when(userRepository.findByEmail("quan@gmail.com")).thenReturn(Optional.of(user));

        // Kiểm tra lỗi 1007 (VERIFICATION_CODE_EXPIRED)
        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.verifyEmail(request);
        });

        assertEquals(ErrorCode.VERIFICATION_CODE_EXPIRED, exception.getErrorCode());
    }

    @Test
    void verifyEmail_UserNotFound_ThrowException() {
        VerifyRequest request = new VerifyRequest();
        request.setEmail("notfound@gmail.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Kiểm tra lỗi 1003 (USER_NOT_FOUND)
        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.verifyEmail(request);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void authenticate_Success() {
        // 1. Giả lập
        AuthenticationRequest request = new AuthenticationRequest("quan@gmail.com", "password123");
        User user = User.builder()
                .email("quan@gmail.com")
                .isVerified(true) // Phải true mới cho vào
                .build();

        when(userRepository.findByEmail("quan@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");

        // 2. Thực thi
        AuthenticationResponse response = authenticationService.authenticate(request);

        // 3. Kiểm chứng
        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        verify(authenticationManager).authenticate(any()); // Phải gọi qua Security Manager để check pass
        verify(userRepository).save(any(User.class)); // Phải lưu Refresh Token vào DB
    }

    @Test
    void authenticate_BadCredentials_ThrowException() {
        AuthenticationRequest request = new AuthenticationRequest("quan@gmail.com", "wrong_pass");

        // Giả lập AuthenticationManager ném lỗi khi sai pass
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid password"));

        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void authenticate_NotVerified_ThrowException() {
        AuthenticationRequest request = new AuthenticationRequest("quan@gmail.com", "password123");
        User user = User.builder()
                .email("quan@gmail.com")
                .isVerified(false) // Tài khoản chưa xác thực
                .build();

        when(userRepository.findByEmail("quan@gmail.com")).thenReturn(Optional.of(user));

        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.authenticate(request);
        });

        assertEquals(ErrorCode.USER_NOT_VERIFIED, exception.getErrorCode());
    }
    @Test
    void refreshToken_Success() {
        // 1. Giả lập
        RefreshTokenRequest request = new RefreshTokenRequest("old_refresh_token");
        User user = User.builder()
                .email("quan@gmail.com")
                .refreshExpiry(LocalDateTime.now().plusDays(1)) // Còn hạn
                .build();

        // Giả lập check nhãn type bên trong token
        when(jwtService.extractClaim(eq("old_refresh_token"), any())).thenReturn("REFRESH");

        // Giả lập tìm thấy user từ RT cũ trong DB
        when(userRepository.findByRefreshToken("old_refresh_token")).thenReturn(Optional.of(user));

        when(jwtService.generateToken(any())).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("new_refresh_token");

        // 2. Thực thi
        AuthenticationResponse response = authenticationService.refreshToken(request);

        // 3. Kiểm chứng
        assertNotNull(response);
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());

        // Quan trọng: Kiểm tra xem RT mới đã được cập nhật vào User object chưa
        assertEquals("new_refresh_token", user.getRefreshToken());
        verify(userRepository).save(user);
    }

    @Test
    void refreshToken_Expired_ThrowException() {
        RefreshTokenRequest request = new RefreshTokenRequest("expired_token");
        User user = User.builder()
                .refreshExpiry(LocalDateTime.now().minusMinutes(1)) // Đã hết hạn
                .build();

        when(jwtService.extractClaim(eq("expired_token"), any())).thenReturn("REFRESH");
        when(userRepository.findByRefreshToken("expired_token")).thenReturn(Optional.of(user));

        assertThrows(AppException.class, () -> {
            authenticationService.refreshToken(request);
        });
    }

    @Test
    void refreshToken_InvalidType_ThrowException() {
        RefreshTokenRequest request = new RefreshTokenRequest("wrong_type_token");

        // Giả lập token gửi lên có nhãn là ACCESS thay vì REFRESH
        when(jwtService.extractClaim(eq("wrong_type_token"), any())).thenReturn("ACCESS");

        AppException exception = assertThrows(AppException.class, () -> {
            authenticationService.refreshToken(request);
        });

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }
}
