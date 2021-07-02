package peterstuck.coursewebsitebackend.exceptions;

public class CourseInvalidDataException extends Exception {

    public CourseInvalidDataException() {
    }

    public CourseInvalidDataException(String message) {
        super(message);
    }
}
