package kz.tlegen.clinic.exception;

public class AppointmentTimeConflictException extends RuntimeException {
    public AppointmentTimeConflictException(String message) {
        super(message);
    }
}
