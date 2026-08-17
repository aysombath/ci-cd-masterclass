package com.substring.springbootapp.repository;

import com.substring.springbootapp.entity.RefreshToken;
import com.substring.springbootapp.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("delete from RefreshToken rt where rt.user = ?1")
    void deleteAllByUser(User user);
}
