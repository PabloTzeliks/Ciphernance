package io.ciphernance.identity.application.exception.user;

import io.ciphernance.identity.application.exception.base.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UUID userId) {
        super("User", userId);
    }

    private UserNotFoundException(String message) {
        super(message);
    }

    public static UserNotFoundException forEmail(String email) {
        return new UserNotFoundException(
                "User not found with email: " + email
        );
    }
}
