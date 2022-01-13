package com.example.todo.service

import com.example.todo.domain.Todo
import com.example.todo.repository.TodoRepository
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val repository: TodoRepository
) {
    fun create(todo: Todo): Todo {
        if (todo.title == null) {
            throw IllegalArgumentException("Title should not be empty")
        }
        return repository.save(todo)
    }
}
