package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.AlreadyCompleteBidException
import com.teamhide.kream.product.application.exception.BiddingNotFoundException
import com.teamhide.kream.product.application.exception.ImmediateTradeAvailableException
import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.event.BiddingCompletedEvent
import com.teamhide.kream.product.domain.event.BiddingCreatedEvent
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.model.InvalidBiddingPriceException
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.domain.usecase.AttemptPaymentUseCase
import com.teamhide.kream.product.domain.usecase.BiddingReaderUseCase
import com.teamhide.kream.product.domain.usecase.CompleteBidUseCase
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import com.teamhide.kream.product.makeBidCommand
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeImmediatePurchaseCommand
import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.user.application.exception.UserNotFoundException
import com.teamhide.kream.user.makeUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk

class BiddingCommandServiceTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val biddingReaderUseCase = mockk<BiddingReaderUseCase>()
    val biddingRepository = mockk<BiddingRepository>()
    val productUserAdapter = mockk<ProductUserAdapter>()
    val productReaderUseCase = mockk<ProductReaderUseCase>()
    val biddingKafkaAdapter = mockk<BiddingKafkaAdapter>()
    val attemptPaymentUseCase = mockk<AttemptPaymentUseCase>()
    val completeBidUseCase = mockk<CompleteBidUseCase>()
    val biddingCommandService = BiddingCommandService(
        biddingReaderUseCase = biddingReaderUseCase,
        biddingRepository = biddingRepository,
        productUserAdapter = productUserAdapter,
        productReaderUseCase = productReaderUseCase,
        biddingKafkaAdapter = biddingKafkaAdapter,
        attemptPaymentUseCase = attemptPaymentUseCase,
        completeBidUseCase = completeBidUseCase,
    )

    Given("일반 입찰 - 동일한 가격의 구매 입찰이 있을 때") {
        val price = 1000
        val purchaseBidding = makeBidding(price = price, biddingType = BiddingType.PURCHASE)
        coEvery { biddingReaderUseCase.findMostExpensiveBidding(any(), any()) } returns purchaseBidding

        val command = makeBidCommand(price = price, biddingType = BiddingType.SALE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("일반 입찰 - 동일한 가격의 판매 입찰이 있을 때") {
        val price = 1000
        val purchaseBidding = makeBidding(price = price, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns purchaseBidding

        val command = makeBidCommand(price = price, biddingType = BiddingType.PURCHASE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("일반 입찰 - 구매 입찰 가격이 가장 낮은 판매 입찰 가격보다 높은 경우") {
        val price = 2000
        val saleBidding = makeBidding(price = price, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns saleBidding

        val command = makeBidCommand(price = 3000, biddingType = BiddingType.PURCHASE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("일반 입찰 - 판매 입찰 가격이 가장 높은 구매 입찰 가격보다 낮은 경우") {
        val price = 3000
        val purchaseBidding = makeBidding(price = price, biddingType = BiddingType.PURCHASE)
        coEvery { biddingReaderUseCase.findMostExpensiveBidding(any(), any()) } returns purchaseBidding

        val command = makeBidCommand(price = 2000, biddingType = BiddingType.SALE)

        When("판매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ImmediateTradeAvailableException> {
                    biddingCommandService.bid(command = command)
                }
            }
        }
    }

    Given("일반 입찰 - 존재하지 않는 유저가") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns saleBidding

        coEvery { productUserAdapter.findById(any()) } returns null

        val command = makeBidCommand(price = 1000, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<UserNotFoundException> { biddingCommandService.bid(command = command) }
            }
        }
    }

    Given("일반 입찰 - 존재하지 않는 상품을 대상으로") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns saleBidding

        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        coEvery { productReaderUseCase.findById(any()) } returns null

        val command = makeBidCommand(price = 1000, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductNotFoundException> { biddingCommandService.bid(command = command) }
            }
        }
    }

    Given("일반 입찰 - 0원 이하의 가격으로") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns saleBidding

        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        val product = makeProduct()
        coEvery { productReaderUseCase.findById(any()) } returns product

        val command = makeBidCommand(price = 0, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidBiddingPriceException> { biddingCommandService.bid(command = command) }
            }
        }
    }

    Given("일반 입찰 - 동일한 판매 가격이 없는 상품을 대상으로") {
        val saleBidding = makeBidding(price = 2000, biddingType = BiddingType.SALE)
        coEvery { biddingReaderUseCase.findMostCheapestBidding(any(), any()) } returns saleBidding

        val user = makeUser()
        coEvery { productUserAdapter.findById(any()) } returns user

        val product = makeProduct()
        coEvery { productReaderUseCase.findById(any()) } returns product

        val biddingPrice = 1000
        val biddingType = BiddingType.PURCHASE
        val bidding = makeBidding(price = biddingPrice, biddingType = biddingType)
        coEvery { biddingRepository.save(any()) } returns bidding

        coEvery { biddingKafkaAdapter.sendBiddingCreated(any<BiddingCreatedEvent>()) } just Runs

        val command = makeBidCommand(price = biddingPrice, biddingType = biddingType)

        When("구매 입찰을 시도하면") {
            val sut = biddingCommandService.bid(command = command)

            Then("성공한다") {
                sut.biddingId shouldBe bidding.id
                sut.price shouldBe command.price
                sut.biddingType shouldBe command.biddingType
            }

            Then("입찰 정보를 저장한다") {
                coVerify(exactly = 1) { biddingRepository.save(any()) }
            }

            Then("입찰 생성 이벤트를 발행한다") {
                coVerify(exactly = 1) { biddingKafkaAdapter.sendBiddingCreated(any<BiddingCreatedEvent>()) }
            }
        }
    }

    Given("일반 입찰 - 존재하지 않는 판매 입찰에 대해") {
        coEvery { biddingReaderUseCase.findById(any()) } returns null

        val command = makeImmediatePurchaseCommand()

        When("즉시 구매 요청을 하면") {
            Then("예외가 발생한다") {
                shouldThrow<BiddingNotFoundException> { biddingCommandService.immediatePurchase(command = command) }
            }
        }
    }

    Given("일반 입찰 - 진행중이 아닌 판매 입찰에 대해") {
        val command = makeImmediatePurchaseCommand()

        val bidding = makeBidding(biddingType = BiddingType.SALE, status = BiddingStatus.COMPLETE)
        coEvery { biddingReaderUseCase.findById(any()) } returns bidding

        When("즉시 구매 요청을 하면") {
            Then("예외가 발생한다") {
                shouldThrow<AlreadyCompleteBidException> { biddingCommandService.immediatePurchase(command = command) }
            }
        }
    }

    Given("즉시 구매 - 존재하지 않는 유저가") {
        val command = makeImmediatePurchaseCommand()

        val bidding = makeBidding(biddingType = BiddingType.SALE, status = BiddingStatus.IN_PROGRESS)
        coEvery { biddingReaderUseCase.findById(any()) } returns bidding

        coEvery { productUserAdapter.findById(any()) } returns null

        When("즉시 구매 요청을 하면") {
            Then("예외가 발생한다") {
                shouldThrow<UserNotFoundException> { biddingCommandService.immediatePurchase(command = command) }
            }
        }
    }

    Given("즉시 구매 - 판매 입찰에 대해") {
        val seller = makeUser(id = 1L)
        coEvery { productUserAdapter.findById(userId = seller.id) } returns seller

        val purchaser = makeUser(id = 2L)
        coEvery { productUserAdapter.findById(userId = purchaser.id) } returns purchaser

        val command = makeImmediatePurchaseCommand(userId = purchaser.id)

        val product = makeProduct(id = 1L)

        val bidding = makeBidding(
            id = command.biddingId,
            productId = product.id,
            userId = seller.id,
            biddingType = BiddingType.SALE,
            status = BiddingStatus.IN_PROGRESS,
        )
        coEvery { biddingReaderUseCase.findById(any()) } returns bidding

        val paymentId = "paymentId"
        coEvery { attemptPaymentUseCase.attemptPayment(any()) } returns paymentId
//        coEvery { pgClient.attemptPayment(any()) } returns AttemptPaymentResponse(paymentId = paymentId)

        coEvery { completeBidUseCase.complete(any()) } just Runs

        coEvery { biddingKafkaAdapter.sendBiddingCompleted(any()) } just Runs

        When("즉시 구매 - 즉시 구매 요청을 하면") {
            val sut = biddingCommandService.immediatePurchase(command = command)

            Then("구매 정보가 리턴된다") {
                sut.biddingId shouldBe bidding.id
                sut.price shouldBe bidding.price
            }

            Then("판매 내역을 생성한다") {
                coVerify(exactly = 1) { attemptPaymentUseCase.attemptPayment(any()) }
            }

            Then("주문을 생성한다") {
                coVerify(exactly = 1) { completeBidUseCase.complete(any()) }
            }

            Then("입찰 종료 이벤트를 보낸다") {
                coVerify(exactly = 1) { biddingKafkaAdapter.sendBiddingCompleted(any<BiddingCompletedEvent>()) }
            }
        }
    }
})
