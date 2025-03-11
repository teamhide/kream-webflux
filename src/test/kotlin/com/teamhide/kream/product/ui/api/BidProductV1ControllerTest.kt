package com.teamhide.kream.product.ui.api

import com.teamhide.kream.product.application.exception.ImmediateTradeAvailableException
import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.model.InvalidBiddingPriceException
import com.teamhide.kream.product.domain.repository.BiddingRepository
import com.teamhide.kream.product.domain.repository.ProductRepository
import com.teamhide.kream.product.makeBidRequest
import com.teamhide.kream.product.makeBidding
import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.support.test.IntegrationTest
import com.teamhide.kream.support.test.TestTokenProvider
import com.teamhide.kream.user.USER_ID_1_TOKEN
import com.teamhide.kream.user.application.exception.UserNotFoundException
import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.blockhound.BlockHound
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

private const val URL = "/v1/bid"

@IntegrationTest
class BidProductV1ControllerTest(
    private val biddingRepository: BiddingRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val testTokenProvider: TestTokenProvider,
    private val webTestClient: WebTestClient,
) : BehaviorSpec({
    extensions(BlockHound())

    afterEach {
        biddingRepository.deleteAll()
        userRepository.deleteAll()
        productRepository.deleteAll()
    }

    Given("동일한 가격의 구매 입찰이 있을 때") {
        val price = 1000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.PURCHASE))

        val request = makeBidRequest(price = price, biddingType = BiddingType.SALE)
        val exc = ImmediateTradeAvailableException()

        When("판매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400 응답이 리턴된다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("동일한 가격의 판매 입찰이 있을 때") {
        val price = 1000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.SALE))

        val request = makeBidRequest(price = price, biddingType = BiddingType.PURCHASE)
        val exc = ImmediateTradeAvailableException()

        When("구매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400 응답이 리턴된다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("구매 입찰 가격이 가장 낮은 판매 입찰 가격보다 높을 때") {
        val price = 2000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.SALE))

        val request = makeBidRequest(price = 3000, biddingType = BiddingType.PURCHASE)
        val exc = ImmediateTradeAvailableException()

        When("판매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400 응답이 리턴된다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("판매 입찰 가격이 가장 높은 구매 입찰 가격보다 낮을 때") {
        val price = 3000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.PURCHASE))

        val request = makeBidRequest(price = 2000, biddingType = BiddingType.SALE)
        val exc = ImmediateTradeAvailableException()

        When("판매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400 응답이 리턴된다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("존재하지 않는 유저가") {
        val price = 2000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.SALE))

        val request = makeBidRequest(price = 1000, biddingType = BiddingType.PURCHASE)
        val exc = UserNotFoundException()

        When("구매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("404 응답이 리턴된다") {
                response.expectStatus().isNotFound
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("존재하지 않는 상품을 대상으로") {
        val price = 2000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.SALE))

        val user = userRepository.save(makeUser())
        val token = testTokenProvider.create(userId = user.id)

        val request = makeBidRequest(price = 1000, biddingType = BiddingType.PURCHASE)
        val exc = ProductNotFoundException()

        When("구매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("404 응답이 리턴된다") {
                response.expectStatus().isNotFound
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("0원 미만의 가격으로") {
        val price = 2000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.SALE))

        val user = userRepository.save(makeUser())
        val token = testTokenProvider.create(userId = user.id)

        val product = productRepository.save(makeProduct())

        val request = makeBidRequest(productId = product.id, price = 0, biddingType = BiddingType.PURCHASE)
        val exc = InvalidBiddingPriceException()

        When("구매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400 응답이 리턴된다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("동일한 판매 가격이 없는 상품을 대상으로") {
        val price = 2000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.SALE))

        val user = userRepository.save(makeUser())
        val token = testTokenProvider.create(userId = user.id)

        val product = productRepository.save(makeProduct())

        val request = makeBidRequest(productId = product.id, price = 1000, biddingType = BiddingType.PURCHASE)

        When("구매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("200 응답이 리턴된다") {
                response.expectStatus().isOk
                    .expectBody()
                    .jsonPath("biddingId").isNotEmpty
                    .jsonPath("price").isEqualTo(request.price)
                    .jsonPath("size").isEqualTo(request.size)
                    .jsonPath("biddingType").isEqualTo(request.biddingType.name)
            }
        }
    }

    Given("동일한 구매 가격이 없는 상품을 대상으로") {
        val price = 2000
        biddingRepository.save(makeBidding(price = price, biddingType = BiddingType.PURCHASE))

        val user = userRepository.save(makeUser())
        val token = testTokenProvider.create(userId = user.id)

        val product = productRepository.save(makeProduct())

        val request = makeBidRequest(productId = product.id, price = 1000, biddingType = BiddingType.SALE)

        When("판매 입찰을 시도하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("200 응답이 리턴된다") {
                response.expectStatus().isOk
                    .expectBody()
                    .jsonPath("biddingId").isNotEmpty
                    .jsonPath("price").isEqualTo(request.price)
                    .jsonPath("size").isEqualTo(request.size)
                    .jsonPath("biddingType").isEqualTo(request.biddingType.name)
            }
        }
    }
})
