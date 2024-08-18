package com.teamhide.kream.product.domain.usecase

data class AttemptPaymentCommand(val biddingId: Long, val price: Int, val userId: Long)

interface AttemptPaymentUseCase {
    suspend fun attemptPayment(command: AttemptPaymentCommand): String
}
