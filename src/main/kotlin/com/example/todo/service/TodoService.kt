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
    fun create(todoDto: TodoDto): Todo {
        return try {
            todoRepository.save(mapToEntity(todoDto))
        } catch(e: MethodArgumentNotValidException) {
            throw APIException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    fun detail(id: Long): Todo {
        return try {
            todoRepository.findById(id).get()
        } catch(e: NoSuchElementException) {
            throw APIException(HttpStatus.NOT_FOUND, "Can't find todo with Id: $id")
        }
    }

    fun list(): List<Todo> {
        return todoRepository.findAll().toList()
    }

    fun update(id: Long, todoDto: TodoDto): Todo {
        val todo = detail(id)
        todo.updateTodo(todoDto.title, todoDto.description)

        return try {
            todoRepository.save(todo)
        } catch (e: Exception) {
            throw APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        }
    }

    fun delete(id: Long): String {
        val todo = detail(id)
        try {
            todoRepository.delete(todo)
            return "Successfully deleted"
        } catch (e: Exception) {
            throw APIException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        }
    }

    private fun mapToEntity(todoDto: TodoDto): Todo {
        return mapper.map(todoDto, Todo::class.java)
    }
}