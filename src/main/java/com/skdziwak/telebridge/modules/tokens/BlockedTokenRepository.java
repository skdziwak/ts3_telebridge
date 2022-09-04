package com.skdziwak.telebridge.modules.tokens;

import com.skdziwak.telebridge.jpa.entities.BlockedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedTokenRepository extends JpaRepository<BlockedToken, Long> {
    Boolean existsByToken(String token);
}
