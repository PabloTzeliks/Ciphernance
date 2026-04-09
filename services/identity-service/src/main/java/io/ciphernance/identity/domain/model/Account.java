package io.ciphernance.identity.domain.model;

import com.fasterxml.uuid.Generators;
import io.ciphernance.identity.domain.exception.InvalidAccountStatusTransitionException;
import io.ciphernance.identity.domain.model.enums.AccountType;
import io.ciphernance.identity.domain.vo.AccountStatus;

import java.time.Instant;
import java.util.UUID;

public class Account {

    private final UUID id;
    private final UUID ownerId;
    private final AccountType type;
    private AccountStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Account(UUID id,
                    UUID ownerId,
                    AccountType type,
                    AccountStatus status,
                    Instant createdAt,
                    Instant updatedAt) {

        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Account createFor(UUID userId,
                                    AccountType type) {

        Instant now = Instant.now();

        return new Account(Generators.timeBasedEpochGenerator().generate(),
                userId,
                type,
                AccountStatus.ACTIVE,
                now,
                now
        );
    }

    public static Account restore(UUID id,
                                  UUID userId,
                                  AccountType type,
                                  AccountStatus status,
                                  Instant createdAt,
                                  Instant updatedAt) {

        return new Account(id, userId, type,
                status, createdAt, updatedAt);
    }

    public void block() {
        transitionTo(AccountStatus.BLOCKED);
    }

    public void underAnalysis() {
        transitionTo(AccountStatus.UNDER_ANALYSIS);
    }

    public void activate() {
        transitionTo(AccountStatus.ACTIVE);
    }

    private void transitionTo(AccountStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new InvalidAccountStatusTransitionException(this.status, next);
        }

        this.status = next;
        this.updatedAt = Instant.now();
    }
}
