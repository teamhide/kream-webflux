package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingType

interface BiddingQueryRepository {
    suspend fun findMostExpensiveBidding(productId: Long, biddingType: BiddingType): Bidding?
    suspend fun findMostCheapestBidding(productId: Long, biddingType: BiddingType): Bidding?
}
