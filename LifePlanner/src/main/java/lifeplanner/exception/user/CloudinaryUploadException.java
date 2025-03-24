package lifeplanner.exception.user;

import lifeplanner.exception.DomainException;

import java.io.IOException;

public class CloudinaryUploadException extends DomainException {
    public CloudinaryUploadException(String message, IOException e) {
        super(message);
    }
}
