package org.example.it211_project_badmintonmanager.service;

import org.example.it211_project_badmintonmanager.entity.TokenBlacklist;
import org.example.it211_project_badmintonmanager.repository.TokenBlacklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    public void addToBlacklist(String token, Date expirationDate) {
        if (!tokenBlacklistRepository.existsByToken(token)) {
            TokenBlacklist blacklistedToken = TokenBlacklist.builder()
                    .token(token)
                    .expirationDate(expirationDate)
                    .build();
            tokenBlacklistRepository.save(blacklistedToken);
        }
    }

    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
}