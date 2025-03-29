package lifeplanner.exception.user;

import lifeplanner.exception.DomainException;

public class UsernameAlreadyExistsException extends DomainException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}