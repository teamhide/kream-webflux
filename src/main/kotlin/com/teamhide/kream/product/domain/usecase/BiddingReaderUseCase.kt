package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingType

interface BiddingReaderUseCase {
    suspend fun findById(biddingId: Long): Bidding?
    suspend fun findMostExpensiveBidding(productId: Long, biddingType: BiddingType): Bidding?
    suspend fun findMostCheapestBidding(productId: Long, biddingType: BiddingType): Bidding?
    suspend fun findBiddingByPriceAndType(price: Int, biddingType: BiddingType): Bidding?
}
