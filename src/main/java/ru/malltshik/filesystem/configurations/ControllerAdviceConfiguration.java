package ru.malltshik.filesystem.configurations;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.malltshik.filesystem.exceptions.BadRequestException;
import ru.malltshik.filesystem.exceptions.ConflictException;
import ru.malltshik.filesystem.exceptions.NotFoundException;

@ControllerAdvice
public class ControllerAdviceConfiguration {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return new ResponseEntity<>(new ExceptionEntity(ex), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleException(ConflictException ex) {
        return new ResponseEntity<>(new ExceptionEntity(ex), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception ex) {
        return new ResponseEntity<>(new ExceptionEntity(ex), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleException(NotFoundException ex) {
        return new ResponseEntity<>(new ExceptionEntity(ex), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @Data @AllArgsConstructor
    class ExceptionEntity {

        private String message;
        private String details;

        ExceptionEntity(Exception exception) {
            this.message = exception.getMessage();
            this.details = exception.getClass().getName();
        }

    }

}
