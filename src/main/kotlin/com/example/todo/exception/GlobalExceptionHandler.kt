package com.example.todo.exception

import com.example.todo.payload.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler
    fun handleApiException(exc: APIException) : ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exc.status.value(), exc.message)
        return ResponseEntity(errorResponse, exc.status)
    }
}