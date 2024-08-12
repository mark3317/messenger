package ru.markn.messenger

data class User(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val age: Int = 1,
    val online: Boolean = false
)
