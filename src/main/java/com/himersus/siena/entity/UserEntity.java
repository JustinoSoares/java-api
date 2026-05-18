package com.himersus.siena.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Data 
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "db_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;
}