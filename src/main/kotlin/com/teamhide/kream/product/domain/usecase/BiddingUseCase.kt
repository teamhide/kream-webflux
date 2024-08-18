package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.BiddingType

data class BidCommand(
    val productId: Long,
    val price: Int,
    val size: String,
    val biddingType: BiddingType,
    val userId: Long,
)

data class BidResponseDto(
    val biddingId: Long,
    val price: Int,
    val size: String,
    val biddingType: BiddingType,
)

data class ImmediatePurchaseCommand(
    val biddingId: Long,
    val userId: Long,
)

data class ImmediatePurchaseResponseDto(
    val biddingId: Long,
    val price: Int,
)

interface BiddingUseCase {
    suspend fun bid(command: BidCommand): BidResponseDto
    suspend fun immediatePurchase(command: ImmediatePurchaseCommand): ImmediatePurchaseResponseDto
}
