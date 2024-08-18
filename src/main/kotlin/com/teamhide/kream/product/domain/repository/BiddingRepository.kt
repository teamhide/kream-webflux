package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface BiddingRepository : CoroutineCrudRepository<Bidding, Long>, BiddingQueryRepository {
    suspend fun findByPriceAndBiddingType(price: Int, biddingType: BiddingType): Bidding?
}
