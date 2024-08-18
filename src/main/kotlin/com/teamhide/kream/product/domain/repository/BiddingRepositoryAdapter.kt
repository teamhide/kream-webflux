package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.model.Order
import com.teamhide.kream.product.domain.model.SaleHistory
import com.teamhide.kream.user.domain.model.User
import org.springframework.stereotype.Component

@Component
class BiddingRepositoryAdapter(
    private val biddingRepository: BiddingRepository,
    private val saleHistoryRepository: SaleHistoryRepository,
    private val orderRepository: OrderRepository,
) {
    suspend fun findBiddingByPriceAndType(price: Int, biddingType: BiddingType): Bidding? {
        return biddingRepository.findByPriceAndBiddingType(price = price, biddingType = biddingType)
    }

    suspend fun save(bidding: Bidding): Bidding {
        return biddingRepository.save(bidding)
    }

    suspend fun findById(biddingId: Long): Bidding? {
        return biddingRepository.findById(biddingId)
    }

    suspend fun saveSaleHistory(bidding: Bidding, user: User): SaleHistory {
        val saleHistory = SaleHistory(
            biddingId = bidding.id,
            userId = user.id,
            price = bidding.price,
            size = bidding.size,
        )
        return saleHistoryRepository.save(saleHistory)
    }

    suspend fun saveOrder(order: Order): Order {
        return orderRepository.save(order)
    }

    suspend fun findMostExpensiveBidding(productId: Long, biddingType: BiddingType): Bidding? {
        return biddingRepository.findMostExpensiveBidding(productId = productId, biddingType = biddingType)
    }

    suspend fun findMostCheapestBidding(productId: Long, biddingType: BiddingType): Bidding? {
        return biddingRepository.findMostCheapestBidding(productId = productId, biddingType = biddingType)
    }
}
