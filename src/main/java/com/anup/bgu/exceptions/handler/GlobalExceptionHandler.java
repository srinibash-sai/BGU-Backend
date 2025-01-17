package com.anup.bgu.exceptions.handler;

import com.anup.bgu.exceptions.dto.ErrorResponse;
import com.anup.bgu.exceptions.models.InvalidImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(InvalidImageException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), ex.getMessage()));
    }
}
