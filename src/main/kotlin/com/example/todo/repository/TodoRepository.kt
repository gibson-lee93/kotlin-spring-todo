package com.example.todo.repository

import com.example.todo.domain.Todo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
}
