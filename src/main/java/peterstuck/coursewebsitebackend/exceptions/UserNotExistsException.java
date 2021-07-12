package peterstuck.coursewebsitebackend.exceptions;

public class UserNotExistsException extends Exception {

    public UserNotExistsException() {
    }

    public UserNotExistsException(String message) {
        super(message);
    }
}
