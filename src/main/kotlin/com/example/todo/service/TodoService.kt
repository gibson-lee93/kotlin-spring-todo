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

    fun detail(id: Long): Todo {
        return try {
            repository.findById(id).get()
        } catch (e: NoSuchElementException) {
            throw NoSuchElementException("Todo does not exist")
        }
    }

    fun update(id: Long, todo: Todo): Todo {
        val foundTodo = detail(id)
        foundTodo.set(todo)
        return repository.save(foundTodo)
    }

    fun delete(id: Long): String {
        val todo = detail(id)
        repository.delete(todo)
        return "Todo successfully deleted"
    }
}
