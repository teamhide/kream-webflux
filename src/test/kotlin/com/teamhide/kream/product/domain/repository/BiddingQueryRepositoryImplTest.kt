package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.support.test.IntegrationTest
import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

@IntegrationTest
class BiddingQueryRepositoryImplTest(
    private val biddingRepository: BiddingRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) : StringSpec({
    afterEach {
        biddingRepository.deleteAll()
        userRepository.deleteAll()
        productRepository.deleteAll()
    }
    "가격과 입찰 타입에 따라 조회한다" {
        // Given
        val user = userRepository.save(makeUser())
        val product = productRepository.save(makeProduct())
        val bidding = biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 1000,
                productId = product.id,
                userId = user.id,
            )
        )

        // When
        val sut = biddingRepository.findByPriceAndBiddingType(price = bidding.price, biddingType = bidding.biddingType)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe bidding.id
        sut.biddingType shouldBe bidding.biddingType
        sut.price shouldBe bidding.price
        sut.status shouldBe bidding.status
        sut.size shouldBe bidding.size
        sut.productId shouldBe bidding.productId
        sut.userId shouldBe bidding.userId
    }

    "특정 상품에 대해 가장 비싼 입찰을 조회한다" {
        // Given
        val user = userRepository.save(makeUser())
        val product = productRepository.save(makeProduct())
        biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 1000,
                productId = product.id,
                userId = user.id,
            )
        )
        biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 2000,
                productId = product.id,
                userId = user.id,
            )
        )
        biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.PURCHASE,
                price = 2000,
                productId = product.id,
                userId = user.id,
            )
        )
        val mostExpensiveBidding = biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 3000,
                productId = product.id,
                userId = user.id,
            )
        )

        // When
        val sut = biddingRepository.findMostExpensiveBidding(productId = product.id, biddingType = BiddingType.SALE)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe mostExpensiveBidding.id
        sut.biddingType shouldBe mostExpensiveBidding.biddingType
        sut.price shouldBe mostExpensiveBidding.price
        sut.status shouldBe mostExpensiveBidding.status
        sut.size shouldBe mostExpensiveBidding.size
        sut.productId shouldBe mostExpensiveBidding.productId
        sut.userId shouldBe mostExpensiveBidding.userId
    }

    "특정 상품에 대해 가장 저렴한 입찰을 조회한다" {
        // Given
        val user = userRepository.save(makeUser())
        val product = productRepository.save(makeProduct())
        val mostCheapestBidding = biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 1000,
                productId = product.id,
                userId = user.id,
            )
        )
        biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 2000,
                productId = product.id,
                userId = user.id,
            )
        )
        biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.PURCHASE,
                price = 2000,
                productId = product.id,
                userId = user.id,
            )
        )
        biddingRepository.save(
            makeBidding(
                biddingType = BiddingType.SALE,
                price = 3000,
                productId = product.id,
                userId = user.id,
            )
        )

        // When
        val sut = biddingRepository.findMostCheapestBidding(productId = product.id, biddingType = BiddingType.SALE)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe mostCheapestBidding.id
        sut.biddingType shouldBe mostCheapestBidding.biddingType
        sut.price shouldBe mostCheapestBidding.price
        sut.status shouldBe mostCheapestBidding.status
        sut.size shouldBe mostCheapestBidding.size
        sut.productId shouldBe mostCheapestBidding.productId
        sut.userId shouldBe mostCheapestBidding.userId
    }
})
