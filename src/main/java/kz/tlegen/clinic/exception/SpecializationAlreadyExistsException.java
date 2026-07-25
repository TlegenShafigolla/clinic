package kz.tlegen.clinic.exception;

public class SpecializationAlreadyExistsException extends RuntimeException {
    public SpecializationAlreadyExistsException(String message) {
        super(message);
    }
}
