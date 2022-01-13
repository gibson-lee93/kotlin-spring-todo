package com.example.todo.controller

import com.example.todo.domain.Todo
import com.example.todo.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(val service: TodoService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody todo: Todo): Todo {
        return service.create(todo)
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable(name = "id") id: Long): Todo {
        return service.detail(id)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id") id: Long,
        @RequestBody todo: Todo
    ): Todo {
        return service.update(id, todo)
    }
}
