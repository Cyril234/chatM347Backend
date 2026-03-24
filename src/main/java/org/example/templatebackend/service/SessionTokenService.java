package org.example.templatebackend.service;

import org.example.templatebackend.repository.SessionToken;
import org.example.templatebackend.repository.SessionTokenRepository;
import org.example.templatebackend.repository.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionTokenService {

    private final SessionTokenRepository sessionTokenRepository;

    public SessionTokenService(SessionTokenRepository sessionTokenRepository) {
        this.sessionTokenRepository = sessionTokenRepository;
    }

    public SessionToken createForUser(User user) {
        sessionTokenRepository.deleteByUser(user);
        SessionToken sessionToken = new SessionToken(UUID.randomUUID().toString(), user, Instant.now());
        return sessionTokenRepository.save(sessionToken);
    }

    public Optional<SessionToken> findByToken(String token) {
        return sessionTokenRepository.findByToken(token);
    }

    public void deleteByToken(String token) {
        sessionTokenRepository.deleteByToken(token);
    }
}
