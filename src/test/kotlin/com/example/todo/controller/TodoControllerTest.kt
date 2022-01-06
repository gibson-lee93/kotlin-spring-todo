package com.example.todo.controller

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.service.TodoService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import io.mockk.every
import org.hamcrest.CoreMatchers.containsString
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TodoController::class)
internal class TodoControllerTest : DescribeSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var todoService: TodoService

    init {
        val mockTodo : Todo = Todo(1, "title", "description")
        val todoDto : TodoDto = TodoDto(1, "title", "description")
        val content : String = ObjectMapper().writeValueAsString(todoDto)

        beforeEach {
            every { todoService.create(todoDto) } returns mockTodo
            every { todoService.detail(1) } returns mockTodo
            every { todoService.list() } returns listOf(mockTodo)
            every { todoService.update(1, todoDto) } returns mockTodo
            every { todoService.delete(1) } returns "Successfully deleted"
        }

        describe("Create a todo") {
            it("responds with created todo") {
                mockMvc.perform(post("/todos")
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)

                todoService.create(todoDto) shouldBe mockTodo
            }
        }

        describe("Get a todo with Id") {
            it("responds with Todo") {
                mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk)

                todoService.detail(1) shouldBe mockTodo
            }
        }

        describe("Get list of todo") {
            it("responds with list of Todo") {
                mockMvc.perform(get("/todos"))
                    .andExpect(status().isOk)

                todoService.list() shouldContain mockTodo
            }
        }

        describe("Update a todo with id") {
            it("responds with updated todo") {
                mockMvc.perform(put("/todos/1")
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)

                todoService.update(1, todoDto) shouldBe mockTodo
            }
        }

        describe("Delete todo with id") {
            it("responds with string when successfully deleted") {
                mockMvc.perform(delete("/todos/1"))
                    .andExpect(status().isOk)
                    .andExpect(content().string(containsString("Successfully")))
            }
        }
    }
}