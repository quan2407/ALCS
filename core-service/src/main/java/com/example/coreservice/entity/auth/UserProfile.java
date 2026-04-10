package com.example.coreservice.entity.auth;

import com.example.coreservice.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String avatarUrl;

    private String timeZone;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
