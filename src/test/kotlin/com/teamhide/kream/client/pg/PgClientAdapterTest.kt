package com.teamhide.kream.client.pg

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.http.HttpStatus

class PgClientAdapterTest : StringSpec({
    val pgClient = mockk<PgClient>()
    val pgClientAdapter = PgClientAdapter(pgClient = pgClient)

    "PgClient를 통한 결제에 성공한다" {
        // Given
        val response = AttemptPaymentResponse(paymentId = "paymentId")
        coEvery { pgClient.attemptPayment(any()) } returns response

        // When
        val sut = pgClientAdapter.attemptPayment(biddingId = 1L, price = 2000, userId = 1L)

        // Then
        sut.isSuccess shouldBe true
        sut.getOrNull() shouldBe response.paymentId
    }

    "PgClient를 통한 결제에 실패한다" {
        // Given
        coEvery { pgClient.attemptPayment(any()) } throws PgClientException(
            statusCode = HttpStatus.BAD_REQUEST, message = ""
        )

        // When
        val sut = pgClientAdapter.attemptPayment(biddingId = 1L, price = 2000, userId = 1L)

        // Then
        sut.isSuccess shouldBe false
    }
})
