package peterstuck.coursewebsitebackend.resources.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AuthAdvisor {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date().getTime());
        body.put("message", "Bad credentials.");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
