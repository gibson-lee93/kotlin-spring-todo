package com.example.todo.service

import com.example.todo.domain.Todo
import com.example.todo.repository.TodoRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

internal class TodoServiceTest : DescribeSpec({
    val repository = mockk<TodoRepository>()

    // todo: 이렇게 해도 가능한가?
    @AnnotationSpec.BeforeEach
    val service = TodoService(repository)

    val todo = Todo(title = "title", description = "description")

    describe("create") {
        context("with a valid parameter") {
            it("returns a created todo") {
                every { repository.save(todo) } returns todo

                val createdTodo = service.create(todo = todo)
                createdTodo.id?.shouldBeGreaterThan(0)
                createdTodo.title shouldBe "title"
                createdTodo.description shouldBe "description"
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
                service.detail(id = 1) shouldBe todo
            }
        }

        context("with a non-existing id") {
            it("throws a not found exception") {
                every { repository.findById(99).get() } throws NoSuchElementException()
                val exception = shouldThrow<NoSuchElementException> {
                    service.detail(id = 99)
                }
                exception.message shouldBe "Todo does not exist"
            }
        }
    }

    describe("list") {
        it("returns list of todos") {
            every { repository.findAll() } returns listOf(todo)
            service.list() shouldContain todo
        }
    }

    describe("update") {
        context("with a valid parameter") {
            it("returns a updated todo") {
                every { repository.findById(1).get() } returns todo
                every { repository.save(todo) } returns todo
                service.update(id = 1, todo = todo) shouldBe todo
            }
        }
    }

    describe("delete") {
        context("with a valid id") {
            it("returns a string when successfully deleted") {
                every { repository.findById(1).get() } returns todo
                every { repository.delete(todo) } returns Unit
                service.delete(id = 1) shouldBe "Todo successfully deleted"
            }
        }
    }
})
