package com.teamhide.kream.client.pg

import com.teamhide.kream.client.WebClientFactory
import com.teamhide.kream.client.requestCatching
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.awaitBody

@Component
class PgClient(
    @Value("\${client.pg.url}")
    private val baseUrl: String,

    @Value("\${client.pg.connect-timeout-milliseconds}")
    private val connectTimeoutMilliSeconds: Int,

    @Value("\${client.pg.read-timeout-milliseconds}")
    private val readTimeoutMilliSeconds: Int,
) {
    private val webClient = WebClientFactory.create(
        clientName = PgClient::class.java.simpleName,
        baseUrl = baseUrl,
        connectTimoutMilliSeconds = connectTimeoutMilliSeconds,
        readTimeoutMilleSeconds = readTimeoutMilliSeconds,
    )

    suspend fun attemptPayment(request: AttemptPaymentRequest): AttemptPaymentResponse {
        return requestCatching {
            webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .awaitBody<AttemptPaymentResponse>()
        }
    }

    suspend fun cancelPayment(request: CancelPaymentRequest) {
        return requestCatching {
            webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .awaitBody()
        }
    }
}
