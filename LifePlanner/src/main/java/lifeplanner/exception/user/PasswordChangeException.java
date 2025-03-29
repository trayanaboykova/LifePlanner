package lifeplanner.exception.user;

import lifeplanner.exception.DomainException;

public class PasswordChangeException extends DomainException {
    public PasswordChangeException(String message) {
        super(message);
    }
}