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

    val todo = Todo(title = "title", description = "description")

    describe("create") {
        context("with a valid parameter") {
            it("returns a created todo") {
                every { repository.save(todo) } returns todo
                service.create(todo) shouldBe todo
                // Todo: 테스트가 헐거움. 뭐를 더 확인해야 할까
            }
        }

        context("with a invalid parameter") {
            val faultyTodo = Todo(description = "description")
            it("throws an illegal argument exception") {
                val exception = shouldThrow<IllegalArgumentException> {
                    service.create(faultyTodo)
                }
                exception.message shouldBe "Title should not be empty"
            }
        }
    }

    describe("detail") {
        context("with a existing id") {
            it("returns a todo") {
                every { repository.findById(1).get() } returns todo
                service.detail(1) shouldBe todo
            }
        }

        // Todo: 테스트가 통과하지 않는다. Exception은 잡는데 response가 internal server error로 나간다.
        context("with an non-existing id") {
            it("throws a not found exception") {
                val exception = shouldThrow<NoSuchElementException> {
                    service.detail(1)
                }
                exception.message shouldBe "Todo does not exist"
            }
        }
    }
})
