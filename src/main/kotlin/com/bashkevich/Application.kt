package com.bashkevich

import com.bashkevich.plugin.configureDependencyInjection
import com.bashkevich.plugin.configureTelegramBot
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureDependencyInjection()
    configureTelegramBot()
}
