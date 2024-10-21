package com.app.chat.data.network

import com.app.chat.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class WebSocketManager: ChatService {

    private val messageFlow = MutableSharedFlow<Message>()

    override suspend fun sendMessage(message: Message) {
        // Send message to the server
    }

    override fun receiveMessages(): Flow<Message> = messageFlow
}