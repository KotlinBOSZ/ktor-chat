package me.szydelko.room

import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.szydelko.data.dto.MessageDto
import me.szydelko.service.MessageDataSource
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.containsKey(username)) {
            throw MemberAlreadyExistsException()
        }
        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(senderUsername: String, message: String){
        members.values.forEach {
            val messageEntity =  MessageDto(
                text = message,
                username = senderUsername,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)
            it.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages() : List<MessageDto>{
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String){
        members[username]?.socket?.close()
        if (members.containsKey(username)){
            members.remove(username)
        }
    }
}