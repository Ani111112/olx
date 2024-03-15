package com.olx.OlxBackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String emailId;
    @Column(unique = true)
    private String phoneNumber;
    private String password;
    private String otp;
    private boolean verified;
}
