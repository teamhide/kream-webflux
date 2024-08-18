package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.makeBidding
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class BiddingReaderTest : StringSpec({
    val biddingRepository = mockk<BiddingRepository>()
    val biddingReader = BiddingReader(biddingRepository = biddingRepository)

    "price와 biddingType으로 Bidding을 조회한다" {
        // Given
        val price = 1000
        val biddingType = BiddingType.SALE
        val bidding = makeBidding()
        coEvery { biddingRepository.findByPriceAndBiddingType(any(), any()) } returns bidding

        // When
        val sut = biddingReader.findBiddingByPriceAndType(price = price, biddingType = biddingType)

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

    "id로 Bidding을 조회한다" {
        // Given
        val biddingId = 1L
        val bidding = makeBidding()
        coEvery { biddingRepository.findById(any()) } returns bidding

        // When
        val sut = biddingReader.findById(biddingId = biddingId)

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

    "가장 비싼 입찰을 조회한다" {
        // Given
        val biddingType = BiddingType.SALE
        val bidding = makeBidding()
        coEvery { biddingRepository.findMostExpensiveBidding(any(), any()) } returns bidding

        // When
        val sut = biddingReader.findMostExpensiveBidding(productId = 1L, biddingType = biddingType)

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
        val sut = biddingReader.findMostCheapestBidding(productId = 1L, biddingType = biddingType)

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
