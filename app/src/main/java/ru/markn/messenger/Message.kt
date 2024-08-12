package ru.markn.messenger

data class Message(
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = ""
)
