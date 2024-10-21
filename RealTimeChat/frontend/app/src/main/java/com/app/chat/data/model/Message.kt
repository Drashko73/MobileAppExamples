package com.app.chat.data.model

data class Message (
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long
)