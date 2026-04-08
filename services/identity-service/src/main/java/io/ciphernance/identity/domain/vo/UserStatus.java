package io.ciphernance.identity.domain.vo;

import java.util.Set;

public enum UserStatus {

    ACTIVE {
        @Override
        public Set<UserStatus> allowedTransitions() {
            return Set.of(SUSPENDED, BLOCKED);
        }
    },

    SUSPENDED {
        @Override
        public Set<UserStatus> allowedTransitions() {
            return Set.of(ACTIVE, BLOCKED);
        }
    },

    BLOCKED {
        @Override
        public Set<UserStatus> allowedTransitions() {
            return Set.of();
        }
    };

    public abstract Set<UserStatus> allowedTransitions();

    public boolean canTransitionTo(UserStatus next) {
        return allowedTransitions().contains(next);
    }
}