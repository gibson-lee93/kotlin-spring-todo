package com.example.todo.controller

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(private val todoService: TodoService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Validated todoDto: TodoDto): Todo { //Dto에서 validation 사용하려면 @Validated 필수
        return todoService.create(todoDto)
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable(name = "id") id: Long): Todo {
        return todoService.detail(id)
    }

    @GetMapping
    fun list(): List<Todo> {
        return todoService.list()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable(name = "id") id: Long,
        @RequestBody @Validated todoDto: TodoDto
    ): Todo {
        return todoService.update(id, todoDto)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable(name = "id") id: Long): String {
        return todoService.delete(id)
    }
}