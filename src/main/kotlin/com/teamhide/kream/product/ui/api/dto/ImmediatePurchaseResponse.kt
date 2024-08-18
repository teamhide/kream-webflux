package com.teamhide.kream.product.ui.api.dto

import com.teamhide.kream.product.domain.usecase.ImmediatePurchaseResponseDto

data class ImmediatePurchaseResponse(
    val biddingId: Long,
    val price: Int,
) {
    companion object {
        fun from(responseDto: ImmediatePurchaseResponseDto): ImmediatePurchaseResponse {
            return responseDto.let {
                ImmediatePurchaseResponse(
                    biddingId = it.biddingId,
                    price = it.price,
                )
            }
        }
    }
}
