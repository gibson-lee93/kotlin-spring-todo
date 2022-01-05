package com.example.todo.entity

import javax.persistence.*

@Entity
@Table(name = "todos")
class Todo(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,

    @Column(nullable = false)
    var title: String,

    var description: String? = null
)