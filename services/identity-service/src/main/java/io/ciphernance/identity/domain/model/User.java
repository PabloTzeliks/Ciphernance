package io.ciphernance.identity.domain.model;

import com.fasterxml.uuid.Generators;
import io.ciphernance.identity.domain.exception.*;
import io.ciphernance.identity.domain.model.enums.UserRole;
import io.ciphernance.identity.domain.vo.Email;
import io.ciphernance.identity.domain.vo.KycLevel;
import io.ciphernance.identity.domain.vo.UserStatus;
import io.ciphernance.identity.domain.vo.Username;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID id;
    private Username username;
    private final Email email;
    private String passwordHash;
    private UserRole role;
    private KycLevel kycLevel;
    private boolean mfaEnabled;
    private String mfaSecret;
    private UserStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UUID id,
                Username username,
                Email email,
                String passwordHash,
                UserRole role,
                 KycLevel kycLevel,
                boolean mfaEnabled,
                String mfaSecret,
                UserStatus status,
                Instant createdAt,
                Instant updatedAt) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.kycLevel = kycLevel;
        this.mfaEnabled = mfaEnabled;
        this.mfaSecret = mfaSecret;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(Username username,
                              Email email,
                              String passwordHash) {

        Instant now = Instant.now();

        return new User(
                Generators.timeBasedEpochGenerator().generate(),
                username,
                email,
                passwordHash,
                UserRole.USER_ROLE,
                KycLevel.KYC_LEVEL_1,
                false,
                null,
                UserStatus.ACTIVE,
                now,
                now
        );
    }

    public static User restore(UUID id,
                               Username username,
                               Email email,
                               String passwordHash,
                               UserRole role,
                               KycLevel kycLevel,
                               boolean mfaEnabled,
                               String mfaSecret,
                               UserStatus status,
                               Instant createdAt,
                               Instant updatedAt) {

        return new User(id, username, email, passwordHash,
                role, kycLevel, mfaEnabled, mfaSecret,
                status, createdAt, updatedAt);
    }

    public void promoteToAdmin() {
        if (this.role == UserRole.ADMIN_ROLE) {
            throw new InvalidRolePromotionException(this.id, this.role);
        }

        this.role = UserRole.ADMIN_ROLE;
        this.updatedAt = Instant.now();
    }

    public void revokeAdmin() {
        if (this.role == UserRole.USER_ROLE) {
            throw new InvalidRoleRevokeException(this.id, this.role);
        }

        this.role = UserRole.USER_ROLE;
        this.updatedAt = Instant.now();
    }

    public void block() {
        transitionTo(UserStatus.BLOCKED);
    }

    public void suspend() {
        transitionTo(UserStatus.SUSPENDED);
    }

    public void activate() {
        transitionTo(UserStatus.ACTIVE);
    }

    private void transitionTo(UserStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new InvalidUserStatusTransitionException(this.status, next);
        }

        this.status = next;
        this.updatedAt = Instant.now();
    }

    public void promoteKyc(KycLevel newLevel) {
        if (newLevel.ordinal() <= this.kycLevel.ordinal()) {
            throw new InvalidKycPromotionException(this.kycLevel, newLevel);
        }

        this.kycLevel = newLevel;
        this.updatedAt = Instant.now();
    }

    public void enableMfa(String secret) {
        if (this.mfaEnabled) {
            throw new MfaAlreadyEnabledException(this.id);
        }

        this.mfaSecret = secret;
        this.mfaEnabled = true;
        this.updatedAt = Instant.now();
    }

    public void disableMfa() {
        if (!this.mfaEnabled) {
            throw new MfaNotEnabledException(this.id);
        }

        this.mfaSecret = null;
        this.mfaEnabled = false;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Username getUsername() { return username; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public KycLevel getKycLevel() { return kycLevel; }
    public boolean isMfaEnabled() { return mfaEnabled; }
    public String getMfaSecret() { return mfaSecret; }
    public UserStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
