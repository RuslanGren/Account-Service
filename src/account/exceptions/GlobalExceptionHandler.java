package account.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<ErrorObject> handleUserExistException(UserExistException ex) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setTimestamp(LocalDate.now());
        errorObject.setStatus(HttpStatus.BAD_REQUEST.value());
        errorObject.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorObject.setMessage(ex.getMessage());

        errorObject.setPath("/api/auth/signup");

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }
}
