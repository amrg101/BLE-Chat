package com.amrg.blechat.ui.screens.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrg.blechat.data.bluetooth.ChatServer
import com.amrg.blechat.domain.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatServer: ChatServer
) : ViewModel() {

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    init {
        viewModelScope.launch {
            chatServer.message.collectLatest {
                if (!_messages.contains(it)) _messages.add(it)
            }
        }
    }

    val currentDevice
        get() = chatServer.currentConnectedDevice

    fun sendMessage(message: String) {
        viewModelScope.launch {
            chatServer.sendMessage(Message.Local(message))
        }
    }

    fun stopServer() = chatServer.stop()
}
