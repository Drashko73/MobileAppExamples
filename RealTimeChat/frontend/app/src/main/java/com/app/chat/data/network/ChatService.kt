package com.app.chat.data.network

import com.app.chat.data.model.Message
import kotlinx.coroutines.flow.Flow

// Flow is a type that can emit multiple values sequentially,
// so it's perfect for representing a stream of messages.

interface ChatService {
    suspend fun sendMessage(message: Message)
    fun receiveMessages(): Flow<Message>
}