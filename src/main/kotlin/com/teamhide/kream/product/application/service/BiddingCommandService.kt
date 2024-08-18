package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.application.exception.ImmediateTradeAvailableException
import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.event.BiddingCreatedEvent
import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.repository.BiddingRepositoryAdapter
import com.teamhide.kream.product.domain.repository.ProductRepositoryAdapter
import com.teamhide.kream.product.domain.usecase.BidCommand
import com.teamhide.kream.product.domain.usecase.BidResponseDto
import com.teamhide.kream.product.domain.usecase.BidUseCase
import com.teamhide.kream.product.domain.usecase.ProductUserAdapter
import com.teamhide.kream.user.application.exception.UserNotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class BiddingCommandService(
    private val biddingRepositoryAdapter: BiddingRepositoryAdapter,
    private val productUserAdapter: ProductUserAdapter,
    private val productRepositoryAdapter: ProductRepositoryAdapter,
    private val applicationEventPublisher: ApplicationEventPublisher,
) : BidUseCase {
    override suspend fun bid(command: BidCommand): BidResponseDto {
        if (!canBid(productId = command.productId, price = command.price, biddingType = command.biddingType)) {
            throw ImmediateTradeAvailableException()
        }

        val userDeferred = CoroutineScope(Dispatchers.IO).async {
            productUserAdapter.findById(userId = command.userId)
                ?: throw UserNotFoundException()
        }
        val productDeferred = CoroutineScope(Dispatchers.IO).async {
            productRepositoryAdapter.findById(productId = command.productId)
                ?: throw ProductNotFoundException()
        }
        val user = userDeferred.await()
        val product = productDeferred.await()

        val bidding = Bidding(
            productId = product.id,
            userId = user.id,
            price = command.price,
            size = command.size,
            status = BiddingStatus.IN_PROGRESS,
            biddingType = command.biddingType,
        )
        val response = biddingRepositoryAdapter.save(bidding).let {
            BidResponseDto(
                biddingId = it.id,
                price = it.price,
                size = it.size,
                biddingType = it.biddingType,
            )
        }

        val event = BiddingCreatedEvent(
            productId = command.productId,
            biddingType = bidding.biddingType.name,
            biddingId = bidding.id,
            price = bidding.price,
        )
        applicationEventPublisher.publishEvent(event)
        return response
    }

    private suspend fun canBid(productId: Long, price: Int, biddingType: BiddingType): Boolean {
        val bidding = getBiddingByType(productId = productId, biddingType = biddingType) ?: return true

        return when {
            price == bidding.price -> false
            biddingType == BiddingType.PURCHASE && price > bidding.price -> false
            biddingType == BiddingType.SALE && price < bidding.price -> false
            else -> true
        }
    }

    private suspend fun getBiddingByType(productId: Long, biddingType: BiddingType): Bidding? {
        return if (biddingType == BiddingType.SALE) {
            biddingRepositoryAdapter.findMostExpensiveBidding(
                productId = productId, biddingType = BiddingType.PURCHASE
            )
        } else {
            biddingRepositoryAdapter.findMostCheapestBidding(
                productId = productId, biddingType = BiddingType.SALE
            )
        }
    }
}
