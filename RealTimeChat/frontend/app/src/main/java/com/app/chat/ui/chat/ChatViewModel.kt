package com.app.chat.ui.chat

import androidx.lifecycle.ViewModel
import com.app.chat.data.model.Message
import com.app.chat.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        viewModelScope.launch {
            chatRepository.receiveMessages().collect { message ->
                _messages.value = _messages.value + message
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            chatRepository.sendMessage(message)
        }
    }
}