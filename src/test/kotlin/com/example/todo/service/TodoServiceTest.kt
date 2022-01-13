package com.example.todo.service

import com.example.todo.domain.Todo
import com.example.todo.repository.TodoRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

internal class TodoServiceTest : DescribeSpec({
    val repository = mockk<TodoRepository>()

    @AnnotationSpec.BeforeEach // todo: 이렇게 해도 가능한가?
    val service = TodoService(repository)

    describe("create") {
        context("with a valid parameter") {
            val todo = Todo(title = "title", description = "description")
            it("returns a created todo") {
                every { repository.save(todo) } returns todo
                service.create(todo) shouldBe todo
                // Todo: 테스트가 헐거움. 뭐를 더 확인해야 할까
            }
        }

        context("with a invalid parameter") {
            val todo = Todo(description = "description")
            it("throws an illegal argument exception") {
                val exception = shouldThrow<IllegalArgumentException> {
                    service.create(todo)
                }
                exception.message shouldBe "Title should not be empty"
            }
        }
    }
})
