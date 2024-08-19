package com.teamhide.kream.client

import com.teamhide.kream.product.domain.usecase.AttemptPaymentCommand

fun makeAttemptPaymentCommand(biddingId: Long = 1L, price: Int = 20000, userId: Long = 1L): AttemptPaymentCommand {
    return AttemptPaymentCommand(
        biddingId = biddingId,
        price = price,
        userId = userId,
    )
}
