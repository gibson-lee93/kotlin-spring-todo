package com.example.todo.controller

import com.example.todo.domain.Todo
import com.example.todo.service.TodoService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.verify
import org.hamcrest.CoreMatchers.containsString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

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
            every { service.create(todo = todo) } returns todo
            every { service.detail(id = 1) } returns todo
            every { service.update(id = 1, todo = todo) } returns todo
            every { service.delete(id = 1) } returns "Todo successfully deleted"
            every { service.list() } returns listOf(todo)
        }

        describe("Create") {
            context("with a valid request body") {
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
                    verify(exactly = 1) { service.detail(id = 1) }
                }
            }
        }

        describe("List") {
            it("responds with a list of todos") {
                mockMvc.perform(get("/todos"))
                    .andExpect(status().isOk)
                verify(exactly = 1) { service.list() }
            }
        }

        describe("Update") {
            context("with a valid request body") {
                val content = objectMapper.writeValueAsString(todo)
                it("responds with a updated todo") {
                    mockMvc.perform(
                        put("/todos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content)
                    )
                        .andExpect(status().isOk)
                    verify(exactly = 1) { service.update(id = 1, todo = todo) }
                }
            }
        }

        describe("Delete") {
            context("with a valid id") {
                it("responds with a string") {
                    mockMvc.perform(delete("/todos/1"))
                        .andExpect(status().isOk)
                        .andExpect(content().string(containsString("successfully")))
                    verify(exactly = 1) { service.delete(id = 1) }
                }
            }
        }
    }
}
