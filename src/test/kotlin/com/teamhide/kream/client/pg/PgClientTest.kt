package com.teamhide.kream.client.pg

import com.fasterxml.jackson.databind.ObjectMapper
import com.teamhide.kream.support.test.WebClientTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@WebClientTest(classes = [PgClient::class, ObjectMapper::class])
class PgClientTest(
    private val pgClient: PgClient,
    private val objectMapper: ObjectMapper,
) : StringSpec({
    var mockWebServer = MockWebServer()

    beforeEach {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    afterEach {
        mockWebServer.close()
    }

    "결제 - 상태 코드가 4XX라면 PgClientException이 발생한다" {
        // Given
        val request = AttemptPaymentRequest(biddingId = 1L, userId = 1L, price = 10000)
        mockWebServer.enqueue(
            MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        )

        // When, Then
        shouldThrow<PgClientException> { pgClient.attemptPayment(request = request) }
    }

    "결제 - 상태 코드가 5XX라면 PgServerException이 발생한다" {
        // Given
        val request = AttemptPaymentRequest(biddingId = 1L, userId = 1L, price = 10000)
        mockWebServer.enqueue(
            MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value())
        )

        // When, Then
        shouldThrow<PgServerException> { pgClient.attemptPayment(request = request) }
    }

    "결제 - 상태 코드가 4XX, 5XX 모두 아니라면 응답을 반환한다" {
        // Given
        val request = AttemptPaymentRequest(biddingId = 1L, userId = 1L, price = 10000)
        val responseBody = AttemptPaymentResponse(paymentId = "paymentId")
        mockWebServer.enqueue(
            MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(responseBody))
        )

        // When
        val response = pgClient.attemptPayment(request = request)

        // Then
        response.paymentId shouldBe responseBody.paymentId
    }

    "결제 취소 - 상태 코드가 4XX라면 PgClientException이 발생한다" {
        // Given
        val request = CancelPaymentRequest(paymentId = "paymentId")
        mockWebServer.enqueue(
            MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
        )

        // When, Then
        shouldThrow<PgClientException> { pgClient.cancelPayment(request = request) }
    }

    "결제 취소 - 상태 코드가 5XX라면 PgClientException이 발생한다" {
        // Given
        val request = CancelPaymentRequest(paymentId = "paymentId")
        mockWebServer.enqueue(
            MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.SERVICE_UNAVAILABLE.value())
        )

        // When, Then
        shouldThrow<PgServerException> { pgClient.cancelPayment(request = request) }
    }

    "결제 취소 - 상태 코드가 4XX, 5XX 모두 아니라면 요청에 성공한다" {
        // Given
        val request = CancelPaymentRequest(paymentId = "paymentId")
        mockWebServer.enqueue(
            MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
        )

        // When, Then
        pgClient.cancelPayment(request = request)
    }
})
