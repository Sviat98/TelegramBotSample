package com.bashkevich.plugin

import com.bashkevich.di.AppModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.ksp.generated.module

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(AppModule().module)
    }
}
