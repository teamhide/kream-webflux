package com.teamhide.kream.client.pg

import org.springframework.stereotype.Component

@Component
class PgClientAdapter(
    private val pgClient: PgClient,
) {
    suspend fun attemptPayment(biddingId: Long, price: Int, userId: Long): Result<String> {
        val request = AttemptPaymentRequest(biddingId = biddingId, price = price, userId = userId)
        return runCatching {
            pgClient.attemptPayment(request = request).paymentId
        }
    }
}
