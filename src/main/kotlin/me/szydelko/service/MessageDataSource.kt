package me.szydelko.service

import me.szydelko.data.dto.MessageDto

interface MessageDataSource {

    suspend fun getAllMessages(): List<MessageDto>

    suspend fun insertMessage(messageDto: MessageDto)
}