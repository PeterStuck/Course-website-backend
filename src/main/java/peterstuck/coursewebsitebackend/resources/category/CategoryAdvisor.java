package peterstuck.coursewebsitebackend.resources.category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import peterstuck.coursewebsitebackend.exceptions.CategoryNotFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CategoryAdvisor {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Object> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date().getTime());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

}
