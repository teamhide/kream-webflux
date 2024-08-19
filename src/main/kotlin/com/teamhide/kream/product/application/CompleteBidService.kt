package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.BiddingNotFoundException
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.model.Order
import com.teamhide.kream.product.domain.model.OrderStatus
import com.teamhide.kream.product.domain.model.SaleHistory
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.domain.repository.OrderRepository
import com.teamhide.kream.product.domain.repository.SaleHistoryRepository
import com.teamhide.kream.product.domain.usecase.CompleteBidCommand
import com.teamhide.kream.product.domain.usecase.CompleteBidUseCase
import com.teamhide.kream.user.application.exception.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CompleteBidService(
    private val biddingRepository: BiddingRepository,
    private val saleHistoryRepository: SaleHistoryRepository,
    private val orderRepository: OrderRepository,
    private val productUserAdapter: ProductUserAdapter,
) : CompleteBidUseCase {
    override suspend fun complete(command: CompleteBidCommand) {
        val user = productUserAdapter.findById(userId = command.userId) ?: throw UserNotFoundException()
        val bidding = biddingRepository.findById(command.biddingId)
            ?: throw BiddingNotFoundException()

        bidding.changeStatus(status = BiddingStatus.COMPLETE)
        biddingRepository.save(bidding)

        val saleHistory = SaleHistory(
            biddingId = bidding.id,
            userId = user.id,
            price = bidding.price,
            size = bidding.size,
        )
        saleHistoryRepository.save(saleHistory)

        val order = Order(
            paymentId = command.paymentId,
            biddingId = bidding.id,
            userId = user.id,
            status = OrderStatus.COMPLETE,
        )
        orderRepository.save(order)
    }
}
