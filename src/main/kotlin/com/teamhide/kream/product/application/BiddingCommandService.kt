package com.teamhide.kream.product.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.teamhide.kream.outbox.AggregateType
import com.teamhide.kream.outbox.Outbox
import com.teamhide.kream.outbox.OutboxRepository
import com.teamhide.kream.product.application.exception.AlreadyCompleteBidException
import com.teamhide.kream.product.application.exception.BiddingNotFoundException
import com.teamhide.kream.product.application.exception.ImmediateTradeAvailableException
import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.event.BiddingCompletedEvent
import com.teamhide.kream.product.domain.event.BiddingCreatedEvent
import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.domain.usecase.AttemptPaymentCommand
import com.teamhide.kream.product.domain.usecase.AttemptPaymentUseCase
import com.teamhide.kream.product.domain.usecase.BidCommand
import com.teamhide.kream.product.domain.usecase.BidResponseDto
import com.teamhide.kream.product.domain.usecase.BiddingReaderUseCase
import com.teamhide.kream.product.domain.usecase.BiddingUseCase
import com.teamhide.kream.product.domain.usecase.CompleteBidCommand
import com.teamhide.kream.product.domain.usecase.CompleteBidUseCase
import com.teamhide.kream.product.domain.usecase.ImmediatePurchaseCommand
import com.teamhide.kream.product.domain.usecase.ImmediatePurchaseResponseDto
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import com.teamhide.kream.user.application.exception.UserNotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.stereotype.Service

@Service
class BiddingCommandService(
    private val biddingReaderUseCase: BiddingReaderUseCase,
    private val biddingRepository: BiddingRepository,
    private val productUserAdapter: ProductUserAdapter,
    private val productReaderUseCase: ProductReaderUseCase,
    private val biddingKafkaAdapter: BiddingKafkaAdapter,
    private val attemptPaymentUseCase: AttemptPaymentUseCase,
    private val completeBidUseCase: CompleteBidUseCase,
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) : BiddingUseCase {
    override suspend fun bid(command: BidCommand): BidResponseDto {
        if (!canBid(productId = command.productId, price = command.price, biddingType = command.biddingType)) {
            throw ImmediateTradeAvailableException()
        }

        val userDeferred = CoroutineScope(Dispatchers.IO).async {
            productUserAdapter.findById(userId = command.userId)
                ?: throw UserNotFoundException()
        }
        val productDeferred = CoroutineScope(Dispatchers.IO).async {
            productReaderUseCase.findById(productId = command.productId)
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
        val response = biddingRepository.save(bidding).let {
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
        biddingKafkaAdapter.sendBiddingCreated(event = event)
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
            biddingReaderUseCase.findMostExpensiveBidding(
                productId = productId, biddingType = BiddingType.PURCHASE
            )
        } else {
            biddingReaderUseCase.findMostCheapestBidding(
                productId = productId, biddingType = BiddingType.SALE
            )
        }
    }

    override suspend fun immediatePurchase(command: ImmediatePurchaseCommand): ImmediatePurchaseResponseDto {
        val bidding =
            biddingReaderUseCase.findById(biddingId = command.biddingId) ?: throw BiddingNotFoundException()
        if (!bidding.canBid()) {
            throw AlreadyCompleteBidException()
        }

        val user =
            productUserAdapter.findById(userId = command.userId) ?: throw UserNotFoundException()

        val purchaserId = user.id
        val attemptPaymentCommand = AttemptPaymentCommand(biddingId = bidding.id, price = bidding.price, userId = purchaserId)
        val paymentId = attemptPaymentUseCase.attemptPayment(command = attemptPaymentCommand)

        val completeBidCommand = CompleteBidCommand(paymentId = paymentId, biddingId = bidding.id, userId = purchaserId)
        completeBidUseCase.complete(command = completeBidCommand)

        saveToOutbox(productId = command.biddingId, biddingId = bidding.id)

        return ImmediatePurchaseResponseDto(biddingId = bidding.id, price = bidding.price)
    }

    private suspend fun saveToOutbox(productId: Long, biddingId: Long) {
        val event = BiddingCompletedEvent(productId = productId, biddingId = biddingId)
        val outbox = Outbox(
            aggregateType = AggregateType.BIDDING_COMPLETED,
            payload = objectMapper.writeValueAsString(event),
        )
        outboxRepository.save(outbox)
    }
}
