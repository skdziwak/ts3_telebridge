package com.skdziwak.telebridge.modules.teamspeak;

import com.skdziwak.telebridge.jpa.entities.TeamspeakUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamspeakUserRepository extends JpaRepository<TeamspeakUser, Long> {
    Optional<TeamspeakUser> findFirstByAliasAndTeamspeakBridgeId(String alias, Long teamspeakBridgeId);
    Optional<TeamspeakUser> findFirstByUserUniqueIDAndTeamspeakBridgeId(String uniqueUserID, Long teamspeakBridgeId);
}
