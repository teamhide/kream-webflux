package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.BiddingNotFoundException
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.domain.repository.OrderRepository
import com.teamhide.kream.product.domain.repository.SaleHistoryRepository
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeCompleteBidCommand
import com.teamhide.kream.product.makeOrder
import com.teamhide.kream.product.makeSaleHistory
import com.teamhide.kream.user.application.exception.UserNotFoundException
import com.teamhide.kream.user.makeUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class CompleteBidServiceTest : BehaviorSpec({
    val biddingRepository = mockk<BiddingRepository>()
    val saleHistoryRepository = mockk<SaleHistoryRepository>()
    val orderRepository = mockk<OrderRepository>()
    val productUserAdapter = mockk<ProductUserAdapter>()
    val completeBidService = CompleteBidService(
        biddingRepository = biddingRepository,
        saleHistoryRepository = saleHistoryRepository,
        orderRepository = orderRepository,
        productUserAdapter = productUserAdapter,
    )

    Given("존재하지 않는 유저가") {
        coEvery { productUserAdapter.findById(any()) } returns null

        val command = makeCompleteBidCommand()

        When("입찰 종료 요청을 하면") {
            Then("예외가 발생한다") {
                shouldThrow<UserNotFoundException> { completeBidService.complete(command = command) }
            }
        }
    }

    Given("존재하지 않는 입찰에 대해") {
        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        coEvery { biddingRepository.findById(any()) } returns null

        val command = makeCompleteBidCommand()

        When("입찰 종료 요청을 하면") {
            Then("예외가 발생한다") {
                shouldThrow<BiddingNotFoundException> { completeBidService.complete(command = command) }
            }
        }
    }

    Given("입찰에 대해") {
        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        val bidding = makeBidding(status = BiddingStatus.IN_PROGRESS)
        coEvery { biddingRepository.findById(any()) } returns bidding

        coEvery { biddingRepository.save(any()) } returns bidding

        val saleHistory = makeSaleHistory()
        coEvery { saleHistoryRepository.save(any()) } returns saleHistory

        val order = makeOrder()
        coEvery { orderRepository.save(any()) } returns order

        val command = makeCompleteBidCommand(biddingId = bidding.id)

        When("입찰 종료 요청을 하면") {
            completeBidService.complete(command = command)

            Then("판매 내역을 생성한다") {
                coVerify(exactly = 1) { saleHistoryRepository.save(any()) }
            }

            Then("주문을 생성한다") {
                coVerify(exactly = 1) { orderRepository.save(any()) }
            }
        }
    }
})
