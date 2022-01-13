package com.example.todo.controller

import com.example.todo.domain.Todo
import com.example.todo.service.TodoService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TodoController::class)
internal class TodoControllerTest : DescribeSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var service: TodoService

    init {
        val todo = Todo(title = "title", description = "description")
        beforeEach {
            every { service.create(todo) } returns todo
            every { service.detail(1) } returns todo
        }

        describe("Create") {
            context("with a valid body") {
                val content = objectMapper.writeValueAsString(todo)
                it("responds with a created todo") {
                    mockMvc.perform(
                        post("/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    )
                        .andExpect(status().isCreated)
                }
            }
        }

        describe("Detail") {
            context("with a valid id") {
                it("responds with a todo") {
                    mockMvc.perform(get("/todos/1"))
                        .andExpect(status().isOk)
                    verify(exactly = 1) { service.detail(1) }
                }
            }
        }
    }
}
