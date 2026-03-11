package com.bashkevich.plugin

import com.bashkevich.di.KoinUserApplication
import io.ktor.server.application.*
import org.koin.ksp.generated.startKoin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        KoinUserApplication.startKoin()
    }
}
