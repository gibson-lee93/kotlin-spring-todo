package com.example.todo.controller

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.service.TodoService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/todos")
class TodoController(private val todoService: TodoService) {

    //Dto에서 validation 사용하려면 @Validated 필수
    @PostMapping
    fun createTodo(@RequestBody @Validated todoDto: TodoDto): Todo {
        return todoService.createTodo(todoDto)
    }
}