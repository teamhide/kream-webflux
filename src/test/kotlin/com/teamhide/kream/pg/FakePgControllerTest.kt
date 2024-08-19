package com.teamhide.kream.pg

import com.teamhide.kream.support.test.IntegrationTest
import io.kotest.core.spec.style.StringSpec
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

private const val URL = "/pg"

@IntegrationTest
class FakePgControllerTest(
    private val webTestClient: WebTestClient,
) : StringSpec({
    "결제를 시도한다" {
        webTestClient.post()
            .uri("$URL/payment")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
    }

    "결제를 취소한다" {
        val request = PgCancelPaymentRequest(paymentId = "paymentId")

        webTestClient.post()
            .uri("$URL/cancel")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isOk
    }
})
