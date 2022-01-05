package com.example.todo.dto

import javax.validation.constraints.Size

data class TodoDto(
    val id: Long,

    @field:Size(min = 4, message = "Todo title should have at least 4 characters")
    var title: String,

    val description: String? = null
) {}