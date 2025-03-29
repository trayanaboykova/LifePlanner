package lifeplanner.exception.user;

import lifeplanner.exception.DomainException;

public class AdminDeletionException extends DomainException {
    public AdminDeletionException(String message) {
        super(message);
    }
}