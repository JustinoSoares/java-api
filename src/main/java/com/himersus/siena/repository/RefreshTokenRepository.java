package com.himersus.siena.repository;

import com.himersus.siena.entity.RefreshToken;
import com.himersus.siena.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUser(UserEntity user);

    @Modifying
    void deleteByUser(UserEntity user);
}
