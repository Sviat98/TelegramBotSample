package com.bashkevich.service

import com.bashkevich.model.QuizEventDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class QuizEventService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        defaultRequest {
            url("https://footballwarsadmin.onrender.com/")
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getQuizEvents(city: String? = null): List<QuizEventDto> = withContext(Dispatchers.IO) {

        client.get("quizEvents"){
          city?.let {
              parameter("city", it)
          }
            parameter("registration_open",true)
        }.body<List<QuizEventDto>>()
    }
}