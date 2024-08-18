package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.usecase.AttemptPaymentCommand
import com.teamhide.kream.product.domain.usecase.AttemptPaymentUseCase
import org.springframework.stereotype.Service

@Service
class AttemptPaymentService : AttemptPaymentUseCase {
    override suspend fun attemptPayment(command: AttemptPaymentCommand): String {
        TODO("Not yet implemented")
    }
}
