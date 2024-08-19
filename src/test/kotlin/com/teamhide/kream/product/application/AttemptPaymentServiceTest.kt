package com.teamhide.kream.product.application

import com.teamhide.kream.client.makeAttemptPaymentCommand
import com.teamhide.kream.client.pg.PgClientAdapter
import com.teamhide.kream.client.pg.PgClientException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import org.springframework.http.HttpStatus

class AttemptPaymentServiceTest : BehaviorSpec({
    val pgClientAdapter = mockk<PgClientAdapter>()
    val attemptPaymentService = AttemptPaymentService(pgClientAdapter = pgClientAdapter)

    Given("PG결제에서 에러가 발생할 때") {
        val command = makeAttemptPaymentCommand()
        coEvery { pgClientAdapter.attemptPayment(any(), any(), any()) } throws PgClientException(
            statusCode = HttpStatus.BAD_REQUEST, message = ""
        )

        When("결제 요청을 진행하면") {
            Then("성공한다") {
                shouldThrow<PgClientException> { attemptPaymentService.attemptPayment(command = command) }
            }
        }
    }

    Given("특정 건에 대해") {
        val command = makeAttemptPaymentCommand()
        coEvery { pgClientAdapter.attemptPayment(any(), any(), any()) } returns Result.success("paymentId")

        When("결제 요청을 진행하면") {
            val sut = attemptPaymentService.attemptPayment(command = command)

            Then("성공한다") {
                sut shouldNotBe null
            }
        }
    }
})
