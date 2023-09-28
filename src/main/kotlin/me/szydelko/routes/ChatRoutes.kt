package me.szydelko.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import me.szydelko.room.MemberAlreadyExistsException
import me.szydelko.room.RoomController
import me.szydelko.session.ChatSession

fun Route.chatSocket(roomController: RoomController) {

    webSocket("/chat-socket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null){
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY,"No session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                username = session.username,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach {frame ->
                if(frame is Frame.Text){
                    roomController.sendMessage(
                        senderUsername = session.username,
                        message = frame.readText()
                    )
                }
            }
        } catch(e: MemberAlreadyExistsException){
            call.respond(
                HttpStatusCode.Conflict
            )
        } catch (e: Exception){
            e.printStackTrace()
        }finally {
            roomController.tryDisconnect(session.username)
        }

    }

}

fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessages()
        )
    }
}