package io.ciphernance.identity.domain.model;

import io.ciphernance.identity.domain.model.enums.UserRole;
import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.Username;

import java.time.Instant;
import java.util.UUID;

public class User {

    private UUID id;
    private Username username;
    private Email email;
    private String passwordHash;
    private UserRole role;
    private boolean mfaEnabled;
    private String mfaSecret;
    private Instant createdAt;
    private Instant updatedAt;

    private User(UUID id,
                Username username,
                Email email,
                String passwordHash,
                UserRole role,
                boolean mfaEnabled,
                String mfaSecret,
                Instant createdAt,
                Instant updatedAt) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
