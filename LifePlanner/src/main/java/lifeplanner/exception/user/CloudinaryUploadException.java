package lifeplanner.exception.user;

import lifeplanner.exception.DomainException;

public class CloudinaryUploadException extends DomainException {
    public CloudinaryUploadException(String message) {
        super(message);
    }
}