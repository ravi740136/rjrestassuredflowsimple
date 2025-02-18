package rj.restassured.flow.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmployeeException(DuplicateEmployeeException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", Instant.now());
        errorDetails.put("status", HttpStatus.CONFLICT.value());
        errorDetails.put("error", "Conflict");
        errorDetails.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(InvalidEmployeeDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEmployeeDataException(InvalidEmployeeDataException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Employee Data", ex.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid employee id", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", Instant.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
    
 // Utility method to avoid code duplication
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", Instant.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", error);
        errorDetails.put("message", message);
        return ResponseEntity.status(status).body(errorDetails);
    }
}

