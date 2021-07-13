package peterstuck.coursewebsitebackend.resources.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import peterstuck.coursewebsitebackend.exceptions.UsernameNotUniqueException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UserAdvisor {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(Exception ex) {
        return new ResponseEntity<>(createResponseContent(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotUniqueException.class)
    public ResponseEntity<Object> handleUsernameNotUniqueException(Exception ex) {
        return new ResponseEntity<>(createResponseContent(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createResponseContent(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date().getTime());
        body.put("message", message);
        return body;
    }

}
