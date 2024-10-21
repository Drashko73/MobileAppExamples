package com.app.chat.data.repository

import com.app.chat.data.model.Message
import com.app.chat.data.network.ChatService

class ChatRepository(private val chatService: ChatService) {

    suspend fun sendMessage(message: Message) {
        chatService.sendMessage(message)
    }

    fun receiveMessages() = chatService.receiveMessages()
}