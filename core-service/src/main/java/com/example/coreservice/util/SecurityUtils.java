package com.example.coreservice.util;

import com.example.coreservice.entity.auth.User;
import com.example.coreservice.enums.ErrorCode;
import com.example.coreservice.exception.AppException;
import com.example.coreservice.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository; // Tự động inject vào đây

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}