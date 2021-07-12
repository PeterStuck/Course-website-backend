package peterstuck.coursewebsitebackend.resources.course;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import peterstuck.coursewebsitebackend.exceptions.CourseNotFoundException;
import peterstuck.coursewebsitebackend.exceptions.NotAnAuthorException;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CourseAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Object> handleCourseNotFoundException(CourseNotFoundException ex) {
        return new ResponseEntity<>(getStandardErrorResponse(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAnAuthorException.class)
    public ResponseEntity<Object> handleNotAnAuthorException(NotAnAuthorException ex) {
        return new ResponseEntity<>(getStandardErrorResponse(ex), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> getStandardErrorResponse(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date().getTime());
        body.put("message", ex.getMessage());

        return body;
    }

}
