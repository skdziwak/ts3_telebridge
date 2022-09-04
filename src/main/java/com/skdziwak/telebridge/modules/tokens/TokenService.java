package com.skdziwak.telebridge.modules.tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skdziwak.telebridge.jpa.entities.BlockedToken;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TokenService {
    private final Logger logger = Logger.getGlobal();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private BlockedTokenRepository blockedTokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public <DATA> String createToken(String type, DATA data, Timestamp activeTo) {
        return Jwts.builder()
                    .claim("data", objectMapper.convertValue(data, Map.class))
                    .claim("type", type)
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .setIssuedAt(new Timestamp(System.currentTimeMillis()))
                    .setExpiration(activeTo)
                    .compact();
    }

    public <DATA> Optional<DATA> getDataFromValidToken(String token, String expectedType, Class<DATA> dataClass) {
        if (blockedTokenRepository.existsByToken(token)) {
            return Optional.empty();
        }
        try {
            Claims body = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            String type = body.get("type", String.class);
            if (!Objects.equals(type, expectedType)) {
                return Optional.empty();
            }
            Map<?, ?> dataMap = body.get("data", Map.class);
            return Optional.of(objectMapper.convertValue(dataMap, dataClass));
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public void invalidateToken(String token) {
        try {
            Claims body = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
            Timestamp activeTo = new Timestamp(body.getExpiration().getTime());
            BlockedToken blockedToken = new BlockedToken();
            blockedToken.setToken(token);
            blockedToken.setActiveTo(activeTo);
            blockedTokenRepository.save(blockedToken);
        } catch (ExpiredJwtException ex) {
            logger.log(Level.INFO, "Skipping expired token invalidation", ex);
        }
    }
}
