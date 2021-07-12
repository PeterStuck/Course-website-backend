package peterstuck.coursewebsitebackend.exceptions;

public class NotAnAuthorException extends Exception {

    public NotAnAuthorException() {
    }

    public NotAnAuthorException(String message) {
        super(message);
    }
}
