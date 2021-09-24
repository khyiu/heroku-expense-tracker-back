package be.kuritsu.hetb.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
