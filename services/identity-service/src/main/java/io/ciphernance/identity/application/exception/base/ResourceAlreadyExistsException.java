package io.ciphernance.identity.application.exception.base;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resource, Object value) {
        super(resource + " already exists: " + value);
    }
}
