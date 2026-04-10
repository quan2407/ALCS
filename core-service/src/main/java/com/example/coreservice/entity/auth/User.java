package com.example.coreservice.entity.auth;

import com.example.coreservice.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(unique = true,nullable = false)
    private String email;

    private boolean isVerified;

    private String passwordHash;

    private String role;

    private String verificationCode;

    private LocalDateTime verificationExpiry;

    private String refreshToken;

    private LocalDateTime refreshExpiry;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private UserProfile profile;
}
