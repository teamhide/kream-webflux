package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.usecase.BiddingReaderUseCase
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.domain.usecase.GetProductDetailQuery
import com.teamhide.kream.product.domain.usecase.ProductDisplayReaderUseCase
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeProductDisplay
import com.teamhide.kream.product.makeProductInfo
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class ProductFinderTest : BehaviorSpec({
    val productDisplayReaderUseCase = mockk<ProductDisplayReaderUseCase>()
    val biddingReaderUseCase = mockk<BiddingReaderUseCase>()
    val productReaderUseCase = mockk<ProductReaderUseCase>()
    val productFinder = ProductFinder(
        productDisplayReaderUseCase = productDisplayReaderUseCase,
        biddingReaderUseCase = biddingReaderUseCase,
        productReaderUseCase = productReaderUseCase,
    )

    Given("page와 size를 통해") {
        val query = GetAllProductQuery(page = 0, size = 20)
        val productDisplay1 = makeProductDisplay(productId = 1L)
        val productDisplay2 = makeProductDisplay(productId = 2L)
        coEvery { productDisplayReaderUseCase.findAllBy(any(), any()) } returns listOf(productDisplay1, productDisplay2)

        When("상품 전시 목록을 요청하면") {
            val sut = productFinder.getAllProducts(query = query)

            Then("전시 목록이 반환된다") {
                sut.size shouldBe 2
                sut[0].productId shouldBe productDisplay1.productId
                sut[0].name shouldBe productDisplay1.name
                sut[0].price shouldBe productDisplay1.price
                sut[0].brand shouldBe productDisplay1.brand
                sut[0].category shouldBe productDisplay1.category

                sut[1].productId shouldBe productDisplay2.productId
                sut[1].name shouldBe productDisplay2.name
                sut[1].price shouldBe productDisplay2.price
                sut[1].brand shouldBe productDisplay2.brand
                sut[1].category shouldBe productDisplay2.category
            }
        }
    }

    Given("없는 상품을 대상으로") {
        val query = GetProductDetailQuery(productId = 1L)
        coEvery { productReaderUseCase.findProductInfoById(any()) } returns null

        When("상세 정보 조회를 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductNotFoundException> {
                    productFinder.getDetailById(query = query)
                }
            }
        }
    }

    Given("입찰 정보가 없는 상품에 대해") {
        val productInfo = makeProductInfo()
        coEvery { productReaderUseCase.findProductInfoById(any()) } returns productInfo

        coEvery { biddingReaderUseCase.findMostExpensiveBidding(any(), any()) } returns null

        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns null

        val query = GetProductDetailQuery(productId = productInfo.productId)

        When("상세 정보 조회를 요청하면") {
            val sut = productFinder.getDetailById(query = query)

            Then("입찰 가격을 제외한 정보를 채워서 리턴한다") {
                sut.shouldNotBeNull()
                sut.productId shouldBe productInfo.productId
                sut.releasePrice shouldBe productInfo.releasePrice
                sut.modelNumber shouldBe productInfo.modelNumber
                sut.name shouldBe productInfo.name
                sut.brand shouldBe productInfo.brand
                sut.category shouldBe productInfo.category
                sut.purchaseBidPrice shouldBe null
                sut.saleBidPrice shouldBe null
            }
        }
    }

    Given("입찰 정보가 존재하는 상품에 대해") {
        val productInfo = makeProductInfo()
        coEvery { productReaderUseCase.findProductInfoById(any()) } returns productInfo

        val mostExpensiveBidding = makeBidding(price = 20000, productId = productInfo.productId, biddingType = BiddingType.PURCHASE)
        coEvery { biddingReaderUseCase.findMostExpensiveBidding(any(), any()) } returns mostExpensiveBidding

        val mostCheapestBidding = makeBidding(price = 1000, productId = productInfo.productId, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns mostCheapestBidding

        val query = GetProductDetailQuery(productId = productInfo.productId)

        When("상세 정보 조회를 요청하면") {
            val sut = productFinder.getDetailById(query = query)

            Then("모든 정보를 채워서 리턴한다") {
                sut.shouldNotBeNull()
                sut.productId shouldBe productInfo.productId
                sut.releasePrice shouldBe productInfo.releasePrice
                sut.modelNumber shouldBe productInfo.modelNumber
                sut.name shouldBe productInfo.name
                sut.brand shouldBe productInfo.brand
                sut.category shouldBe productInfo.category
                sut.purchaseBidPrice shouldBe mostCheapestBidding.price
                sut.saleBidPrice shouldBe mostExpensiveBidding.price
            }
        }
    }
})
