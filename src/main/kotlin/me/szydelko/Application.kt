package me.szydelko

import io.ktor.server.application.*
import io.ktor.server.application.*
import me.szydelko.di.mainModule
import me.szydelko.plugins.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin){
        modules(mainModule)
    }
    configureSecurity()
    configureSerialization()
    configureMonitoring()
    configureSockets()
    configureRouting()
}
