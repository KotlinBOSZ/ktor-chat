package me.szydelko.di

import me.szydelko.data.dao.MessageService
import me.szydelko.room.RoomController
import me.szydelko.service.MessageDataSource
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val mainModule = module {
    single {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    }
    single<MessageDataSource> {
        MessageService(get())
    }
    single {
        RoomController(get())
    }
}