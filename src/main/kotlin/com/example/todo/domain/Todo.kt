package com.example.todo.domain

import javax.persistence.*

@Entity
@Table(name = "todos")
data class Todo(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String? = null,

    var description: String? = null
)
