package com.example.todo.exception

import org.springframework.http.HttpStatus

class APIException(status: HttpStatus, message : String) : RuntimeException(message) {
    val status = status
}