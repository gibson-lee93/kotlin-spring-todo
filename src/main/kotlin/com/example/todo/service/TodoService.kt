package com.example.todo.service

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.exception.APIException
import com.example.todo.repository.TodoRepository
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.MethodArgumentNotValidException

@Service
class TodoService(
    private val todoRepository: TodoRepository,
    private val mapper: ModelMapper
) {
    fun createTodo(todoDto: TodoDto): Todo {
        return try {
            todoRepository.save(mapToEntity(todoDto))
        } catch(e: MethodArgumentNotValidException) {
            throw APIException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    fun getTodoById(id: Long): Todo {
        return try {
            todoRepository.findById(id).get()
        } catch(e: NoSuchElementException) {
            throw APIException(HttpStatus.NOT_FOUND, "Not found")
        }
    }

    fun getAllTodos(): List<Todo> {
        return todoRepository.findAll().toList()
    }

    private fun mapToEntity(todoDto: TodoDto): Todo {
        return mapper.map(todoDto, Todo::class.java)
    }
}