package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.application.exception.ImmediateTradeAvailableException
import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.event.BiddingCreatedEvent
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.model.InvalidBiddingPriceException
import com.teamhide.kream.product.domain.repository.BiddingRepositoryAdapter
import com.teamhide.kream.product.domain.repository.ProductRepositoryAdapter
import com.teamhide.kream.product.domain.usecase.ProductUserAdapter
import com.teamhide.kream.product.makeBidCommand
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.user.application.exception.UserNotFoundException
import com.teamhide.kream.user.makeUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import org.springframework.context.ApplicationEventPublisher

class BiddingCommandServiceTest : BehaviorSpec({
    val biddingRepositoryAdapter = mockk<BiddingRepositoryAdapter>()
    val productUserAdapter = mockk<ProductUserAdapter>()
    val productRepositoryAdapter = mockk<ProductRepositoryAdapter>()
    val applicationEventPublisher = mockk<ApplicationEventPublisher>()
    val biddingCommandService = BiddingCommandService(
        biddingRepositoryAdapter = biddingRepositoryAdapter,
        productUserAdapter = productUserAdapter,
        productRepositoryAdapter = productRepositoryAdapter,
        applicationEventPublisher = applicationEventPublisher,
    )

    Given("동일한 가격의 구매 입찰이 있을 때") {
        val price = 1000
        val purchaseBidding = makeBidding(price = price, biddingType = BiddingType.PURCHASE)
        coEvery { biddingRepositoryAdapter.findMostExpensiveBidding(any(), any()) } returns purchaseBidding

        val command = makeBidCommand(price = price, biddingType = BiddingType.SALE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("동일한 가격의 판매 입찰이 있을 때") {
        val price = 1000
        val purchaseBidding = makeBidding(price = price, biddingType = BiddingType.SALE)
        coEvery { biddingRepositoryAdapter.findMostCheapestBidding(any(), any()) } returns purchaseBidding

        val command = makeBidCommand(price = price, biddingType = BiddingType.PURCHASE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("구매 입찰 가격이 가장 낮은 판매 입찰 가격보다 높은 경우") {
        val price = 2000
        val saleBidding = makeBidding(price = price, biddingType = BiddingType.SALE)
        coEvery { biddingRepositoryAdapter.findMostCheapestBidding(any(), any()) } returns saleBidding

        val command = makeBidCommand(price = 3000, biddingType = BiddingType.PURCHASE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("판매 입찰 가격이 가장 높은 구매 입찰 가격보다 낮은 경우") {
        val price = 3000
        val purchaseBidding = makeBidding(price = price, biddingType = BiddingType.PURCHASE)
        coEvery { biddingRepositoryAdapter.findMostExpensiveBidding(any(), any()) } returns purchaseBidding

        val command = makeBidCommand(price = 2000, biddingType = BiddingType.SALE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("존재하지 않는 유저가") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingRepositoryAdapter.findMostCheapestBidding(any(), any()) } returns saleBidding

        coEvery { productUserAdapter.findById(any()) } returns null

        val command = makeBidCommand(price = 1000, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<UserNotFoundException> { biddingCommandService.bid(command = command) }
            }
        }
    }

    Given("존재하지 않는 상품을 대상으로") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingRepositoryAdapter.findMostCheapestBidding(any(), any()) } returns saleBidding

        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        coEvery { productRepositoryAdapter.findById(any()) } returns null

        val command = makeBidCommand(price = 1000, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductNotFoundException> { biddingCommandService.bid(command = command) }
            }
        }
    }

    Given("0원 이하의 가격으로") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingRepositoryAdapter.findMostCheapestBidding(any(), any()) } returns saleBidding

        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        val product = makeProduct()
        coEvery { productRepositoryAdapter.findById(any()) } returns product

        val command = makeBidCommand(price = 0, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidBiddingPriceException> { biddingCommandService.bid(command = command) }
            }
        }
    }

    Given("동일한 판매 가격이 없는 상품을 대상으로") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingRepositoryAdapter.findMostCheapestBidding(any(), any()) } returns saleBidding

        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        val product = makeProduct()
        coEvery { productRepositoryAdapter.findById(any()) } returns product

        val biddingPrice = 1000
        val biddingType = BiddingType.PURCHASE
        val bidding = makeBidding(price = biddingPrice, biddingType = biddingType)
        coEvery { biddingRepositoryAdapter.save(any()) } returns bidding

        coEvery { applicationEventPublisher.publishEvent(any<BiddingCreatedEvent>()) } just Runs

        val command = makeBidCommand(price = biddingPrice, biddingType = biddingType)

        When("구매 입찰을 시도하면") {
            val sut = biddingCommandService.bid(command = command)

            Then("성공한다") {
                sut.biddingId shouldBe bidding.id
                sut.price shouldBe command.price
                sut.biddingType shouldBe command.biddingType
            }

            Then("입찰 정보를 저장한다") {
                coVerify(exactly = 1) { biddingRepositoryAdapter.save(any()) }
            }

            Then("입찰 생성 이벤트를 발행한다") {
                coVerify(exactly = 1) { applicationEventPublisher.publishEvent(any<BiddingCreatedEvent>()) }
            }
        }
    }
})
