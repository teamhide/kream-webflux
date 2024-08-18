package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.domain.usecase.BiddingReaderUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BiddingReader(
    private val biddingRepository: BiddingRepository,
) : BiddingReaderUseCase {
    override suspend fun findById(biddingId: Long): Bidding? {
        return biddingRepository.findById(biddingId)
    }

    override suspend fun findMostExpensiveBidding(productId: Long, biddingType: BiddingType): Bidding? {
        return biddingRepository.findMostExpensiveBidding(productId = productId, biddingType = biddingType)
    }

    override suspend fun findMostCheapestBidding(productId: Long, biddingType: BiddingType): Bidding? {
        return biddingRepository.findMostCheapestBidding(productId = productId, biddingType = biddingType)
    }

    override suspend fun findBiddingByPriceAndType(price: Int, biddingType: BiddingType): Bidding? {
        return biddingRepository.findByPriceAndBiddingType(price = price, biddingType = biddingType)
    }
}
