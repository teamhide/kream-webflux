package com.teamhide.kream.product.ui.api.dto

import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.usecase.BidResponseDto

data class BidResponse(
    val biddingId: Long,
    val price: Int,
    val size: String,
    val biddingType: BiddingType,
) {
    companion object {
        fun from(bidResponseDto: BidResponseDto): BidResponse {
            return bidResponseDto.let {
                BidResponse(
                    biddingId = it.biddingId,
                    price = it.price,
                    size = it.size,
                    biddingType = it.biddingType,
                )
            }
        }
    }
}
