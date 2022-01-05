package com.example.todo.controller

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.service.TodoService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(private val todoService: TodoService) {

    //Dto에서 validation 사용하려면 @Validated 필수
    @PostMapping
    fun createTodo(@RequestBody @Validated todoDto: TodoDto): Todo {
        return todoService.createTodo(todoDto)
    }

    @GetMapping("/{id}")
    fun getTodoById(@PathVariable(name = "id") id: Long): Todo {
        return todoService.getTodoById(id)
    }

    @GetMapping
    fun getAllTodos(): List<Todo> {
        return todoService.getAllTodos()
    }

    @PutMapping("/{id}")
    fun updateTodo(
        @PathVariable(name = "id") id: Long,
        @RequestBody @Validated todoDto: TodoDto
    ): Todo {
        return todoService.updateTodo(id, todoDto)
    }
}