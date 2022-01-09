package com.example.todo.controller

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.exception.APIException
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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TodoController::class)
internal class TodoControllerTest : DescribeSpec() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var service: TodoService

    init {
        val todo : Todo = Todo(1, "title", "description")
        val todoDto : TodoDto = TodoDto(1, "title", "description")
        val dtoContent : String = ObjectMapper().writeValueAsString(todoDto)

        describe("Create a todo") {
            it("Responds with a created todo") {
                every { service.create(todoDto) } returns todo
                mockMvc.perform(post("/todos")
                    .content(dtoContent)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)

                service.create(todoDto) shouldBe todo
            }

            it("Responds with a bad request exception error when given invalid argument") {
                val faultyDto : TodoDto = TodoDto(1, "ti", "description")
                val faultyContent : String = ObjectMapper().writeValueAsString(faultyDto)
                mockMvc.perform(post("/todos")
                    .content(faultyContent)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest)
            }
        }

        describe("Get a todo with Id") {
            it("Responds with a todo") {
                every { service.detail(1) } returns todo
                mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isOk)

                service.detail(1) shouldBe todo
            }

            it("Responds with not found exception error when a todo with given id is not found") {
                every { service.detail(1) } throws APIException(
                    HttpStatus.NOT_FOUND,
                    "Can't find todo with Id: 1")
                mockMvc.perform(get("/todos/1"))
                    .andExpect(status().isNotFound)
            }
        }

        describe("Get list of todo") {
            it("Responds with a list of Todo") {
                every { service.list() } returns listOf(todo)
                mockMvc.perform(get("/todos"))
                    .andExpect(status().isOk)

                service.list() shouldContain todo
            }
        }

        describe("Update a todo with id") {
            it("Responds with a updated todo") {
                every { service.update(1, todoDto) } returns todo
                mockMvc.perform(put("/todos/1")
                    .content(dtoContent)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)

                service.update(1, todoDto) shouldBe todo
            }
        }

        describe("Delete a todo with id") {
            it("Responds with a string when successfully deleted") {
                every { service.delete(1) } returns "Successfully deleted"
                mockMvc.perform(delete("/todos/1"))
                    .andExpect(status().isOk)
                    .andExpect(content().string(containsString("Successfully")))
            }
        }
    }
}
