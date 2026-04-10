package io.ciphernance.identity.domain.vo;

import java.util.Set;

public enum AccountStatus {

    ACTIVE {
        @Override
        public Set<AccountStatus> allowedTransitions() {
            return Set.of(UNDER_ANALYSIS, BLOCKED);
        }
    },

    BLOCKED {
        @Override
        public Set<AccountStatus> allowedTransitions() {
            return Set.of();
        }
    },

    UNDER_ANALYSIS {
        @Override
        public Set<AccountStatus> allowedTransitions() {
            return Set.of(ACTIVE, BLOCKED);
        }
    };

    public abstract Set<AccountStatus> allowedTransitions();

    public boolean canTransitionTo(AccountStatus next) {
        return allowedTransitions().contains(next);
    }
}
