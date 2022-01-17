package com.example.todo.exception

data class ExceptionResponse(
    val code: String?,
    val message: String?,
    val trace: String?
)