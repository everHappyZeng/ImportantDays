package com.example.importantdays.domain.model

data class Person(
    val id: Long = 0,
    val name: String,
    val avatar: String? = null,
    val notes: String = "",
    val hobbies: List<String> = listOf(),
    val createdAt: Long = System.currentTimeMillis()
)
