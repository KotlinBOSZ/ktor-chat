package me.szydelko.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.szydelko.room.RoomController
import me.szydelko.routes.chatSocket
import me.szydelko.routes.getAllMessages
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    routing {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
