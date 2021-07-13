package peterstuck.coursewebsitebackend.exceptions;

public class UsernameNotUniqueException extends Exception {

    public UsernameNotUniqueException() {
    }

    public UsernameNotUniqueException(String message) {
        super(message);
    }
}
