package com.bashkevich.service

import com.bashkevich.model.CounterDto
import com.bashkevich.model.CreateCounterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class CounterApiService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun createCounter(name: String): CounterDto = withContext(Dispatchers.IO) {
        client.post("https://api.tennisscorekeeper.tech/counters") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Origin, "https://tennisscorekeeper.tech")
            setBody(CreateCounterRequest(name))
        }.body<CounterDto>()
    }
}
