package faang.school.notificationservice.exception.handler;

import faang.school.notificationservice.exception.VonageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(VonageException.class)
    public ResponseEntity<?> handleVonageException(VonageException ex) {
        return handleExceptionInternal(ex, INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> handleExceptionInternal(Exception ex, HttpStatus status) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}
