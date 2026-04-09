package com.example.coreservice.exception;

import com.example.coreservice.dto.ErrorResponse;
import com.example.coreservice.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, WebRequest request){
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse error = ErrorResponse.builder()
                .status(errorCode.getStatusCode().value())
                .error(errorCode.getStatusCode().toString())
                .message(errorCode.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, errorCode.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request){
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCode.getStatusCode().value())
                .error(errorCode.name())
                .message(ex.getMessage() != null ? ex.getMessage() : errorCode.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatusCode());
    }
}
