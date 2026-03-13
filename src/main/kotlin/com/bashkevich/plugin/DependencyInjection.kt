package com.bashkevich.plugin

import io.ktor.server.application.*
import org.koin.core.annotation.KoinApplication
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.plugin.module.dsl.withConfiguration

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        withConfiguration<KoinUserApplication>()
    }
}

@KoinApplication
object KoinUserApplication
