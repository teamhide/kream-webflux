package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeOrder
import com.teamhide.kream.product.makeSaleHistory
import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class BiddingRepositoryAdapterTest : StringSpec({
    val biddingRepository = mockk<BiddingRepository>()
    val saleHistoryRepository = mockk<SaleHistoryRepository>()
    val orderRepository = mockk<OrderRepository>()
    val biddingRepositoryAdapter = BiddingRepositoryAdapter(
        orderRepository = orderRepository,
        biddingRepository = biddingRepository,
        saleHistoryRepository = saleHistoryRepository,
    )

    "price와 biddingType으로 Bidding을 조회한다" {
        // Given
        val price = 1000
        val biddingType = BiddingType.SALE
        val bidding = makeBidding()
        coEvery { biddingRepository.findByPriceAndBiddingType(any(), any()) } returns bidding

        // When
        val sut = biddingRepositoryAdapter.findBiddingByPriceAndType(price = price, biddingType = biddingType)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe bidding.id
        sut.biddingType shouldBe bidding.biddingType
        sut.userId shouldBe bidding.userId
        sut.productId shouldBe bidding.productId
        sut.price shouldBe bidding.price
        sut.size shouldBe bidding.size
        sut.status shouldBe bidding.status
        coVerify(exactly = 1) { biddingRepository.findByPriceAndBiddingType(any(), any()) }
    }

    "Bidding을 저장한다" {
        // Given
        val bidding = makeBidding()
        coEvery { biddingRepository.save(any()) } returns bidding

        // When
        val sut = biddingRepositoryAdapter.save(bidding)

        // Then
        sut.id shouldBe bidding.id
        sut.biddingType shouldBe bidding.biddingType
        sut.userId shouldBe bidding.userId
        sut.productId shouldBe bidding.productId
        sut.price shouldBe bidding.price
        sut.size shouldBe bidding.size
        sut.status shouldBe bidding.status
        coVerify(exactly = 1) { biddingRepository.save(any()) }
    }

    "id로 Bidding을 조회한다" {
        // Given
        val biddingId = 1L
        val bidding = makeBidding()
        coEvery { biddingRepository.findById(any()) } returns bidding

        // When
        val sut = biddingRepositoryAdapter.findById(biddingId = biddingId)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe bidding.id
        sut.biddingType shouldBe bidding.biddingType
        sut.userId shouldBe bidding.userId
        sut.productId shouldBe bidding.productId
        sut.price shouldBe bidding.price
        sut.size shouldBe bidding.size
        sut.status shouldBe bidding.status
    }

    "Order를 저장한다" {
        // Given
        val order = makeOrder()
        coEvery { orderRepository.save(any()) } returns order

        // When
        val sut = biddingRepositoryAdapter.saveOrder(order = order)

        // Then
        sut.id shouldBe order.id
        sut.biddingId shouldBe order.biddingId
        sut.userId shouldBe order.userId
        sut.paymentId shouldBe order.paymentId
        sut.status shouldBe order.status
    }

    "SaleHistory를 저장한다" {
        // Given
        val bidding = makeBidding()
        val user = makeUser()
        val saleHistory = makeSaleHistory()
        coEvery { saleHistoryRepository.save(any()) } returns saleHistory

        // When
        val sut = biddingRepositoryAdapter.saveSaleHistory(bidding = bidding, user = user)

        // Then
        sut.id shouldBe saleHistory.id
        sut.biddingId shouldBe saleHistory.biddingId
        sut.userId shouldBe saleHistory.userId
        sut.price shouldBe saleHistory.price
        sut.size shouldBe saleHistory.size
    }

    "가장 비싼 입찰을 조회한다" {
        // Given
        val biddingType = BiddingType.SALE
        val bidding = makeBidding()
        coEvery { biddingRepository.findMostExpensiveBidding(any(), any()) } returns bidding

        // When
        val sut = biddingRepositoryAdapter.findMostExpensiveBidding(productId = 1L, biddingType = biddingType)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe bidding.id
        sut.biddingType shouldBe bidding.biddingType
        sut.userId shouldBe bidding.userId
        sut.productId shouldBe bidding.productId
        sut.price shouldBe bidding.price
        sut.size shouldBe bidding.size
        sut.status shouldBe bidding.status
        coVerify(exactly = 1) { biddingRepository.findMostExpensiveBidding(any(), any()) }
    }

    "가장 저렴한 입찰을 조회한다" {
        // Given
        val biddingType = BiddingType.SALE
        val bidding = makeBidding()
        coEvery { biddingRepository.findMostCheapestBidding(any(), any()) } returns bidding

        // When
        val sut = biddingRepositoryAdapter.findMostCheapestBidding(productId = 1L, biddingType = biddingType)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe bidding.id
        sut.biddingType shouldBe bidding.biddingType
        sut.userId shouldBe bidding.userId
        sut.productId shouldBe bidding.productId
        sut.price shouldBe bidding.price
        sut.size shouldBe bidding.size
        sut.status shouldBe bidding.status
        coVerify(exactly = 1) { biddingRepository.findMostCheapestBidding(any(), any()) }
    }
})
