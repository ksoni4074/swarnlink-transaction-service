package com.swarnlink.aop;

import com.swarnlink.dtos.ErrorResponse;
import com.swarnlink.exceptions.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "Access Denied", HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "Bad Request", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity
                .badRequest()
                .body("Missing required header: " + ex.getHeaderName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "Invalid Argument", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex, HttpServletRequest request) {
        log.error("Unhandled exception: ", ex);
        return buildErrorResponse("Something went wrong", "External Client Issue", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: ", ex);
        return buildErrorResponse("Something went wrong", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, String error, HttpStatus status, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                message,
                error,
                status.value(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, status);
    }
}