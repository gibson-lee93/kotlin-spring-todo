package com.example.todo.service

import com.example.todo.dto.TodoDto
import com.example.todo.entity.Todo
import com.example.todo.exception.APIException
import com.example.todo.repository.TodoRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.modelmapper.ModelMapper
import org.springframework.http.HttpStatus

internal class TodoServiceTest : DescribeSpec({
    val repository = mockk<TodoRepository>()
    val mapper = mockk<ModelMapper>()
    val service = TodoService(repository, mapper)

    val todo : Todo = Todo(1, "title", "description")
    val todoDto : TodoDto = TodoDto(1, "title", "description")

    beforeTest {
        every { repository.findById(1).get() } returns todo
    }

    describe("Create") {
        it("Returns a created todo") {
            every { service.mapToEntity(todoDto) } returns todo
            every { repository.save(todo) } returns todo
            service.create(todoDto) shouldBe todo
        }
    }

    describe("Detail") {
        it("Returns a todo") {
            service.detail(1) shouldBe todo
        }

        it("Throws a not found exception error") {
            val exception = shouldThrow<APIException> {
                every { repository.findById(1).get() } throws APIException(
                    HttpStatus.NOT_FOUND,
                    "Can't find todo with Id: 1")
                service.detail(1)
            }
            exception.message shouldBe "Can't find todo with Id: 1"
        }
    }

    describe("List") {
        it("Returns a list of todo") {
            every { repository.findAll() } returns listOf(todo)
            service.list() shouldContain todo
        }
    }

    describe("Update") {
        it("Returns with updated todo") {
            every { repository.save(todo) } returns todo
            service.update(1, todoDto) shouldBe todo
        }
    }

    describe("Delete") {
        it("Returns a string when successfully deleted") {
            every { repository.delete(todo) } returns Unit
            service.delete(1) shouldBe "Successfully deleted"
        }
    }
})
