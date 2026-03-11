package com.bashkevich.di

import com.bashkevich.service.CounterApiService
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.annotation.KoinApplication

@KoinApplication
object KoinUserApplication

@Module
class AppModule {
    @Single
    fun counterApiService() = CounterApiService()
}
