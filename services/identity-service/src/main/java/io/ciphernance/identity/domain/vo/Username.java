package io.ciphernance.identity.domain.vo;

import io.ciphernance.identity.domain.exception.InvalidUsernameException;
import java.util.regex.Pattern;

public final class Username {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    private final String value;

    public Username(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUsernameException("Username must not be blank");
        }
        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new InvalidUsernameException(
                    "Username must be 3-50 characters, alphanumeric and underscore only: " + value
            );
        }
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username username)) return false;
        return value.equals(username.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}