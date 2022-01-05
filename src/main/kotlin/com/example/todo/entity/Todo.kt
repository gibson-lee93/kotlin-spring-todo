package com.example.todo.entity

import javax.persistence.*

@Entity
@Table(name = "todos")
class Todo(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    val description: String? = null
)