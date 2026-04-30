package com.random.app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", e.getCode());
        body.put("msg", e.getMessage());
        body.put("data", null);
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        FieldError fieldError = e.getBindingResult().getFieldError();
        body.put("msg", fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败");
        body.put("data", null);
        return ResponseEntity.ok(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 0);
        body.put("msg", "服务器内部错误: " + e.getMessage());
        body.put("data", null);
        return ResponseEntity.status(500).body(body);
    }
}
