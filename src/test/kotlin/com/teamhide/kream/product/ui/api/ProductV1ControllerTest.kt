package com.teamhide.kream.product.ui.api

import com.teamhide.kream.product.application.exception.ProductBrandNotFoundException
import com.teamhide.kream.product.application.exception.ProductCategoryNotFoundException
import com.teamhide.kream.product.domain.model.InvalidReleasePriceException
import com.teamhide.kream.product.domain.repository.ProductBrandRepository
import com.teamhide.kream.product.domain.repository.ProductCategoryRepository
import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.domain.repository.ProductRepository
import com.teamhide.kream.product.makeProductBrand
import com.teamhide.kream.product.makeProductCategory
import com.teamhide.kream.product.makeProductDisplay
import com.teamhide.kream.product.makeRegisterProductRequest
import com.teamhide.kream.support.test.IntegrationTest
import com.teamhide.kream.user.USER_ID_1_TOKEN
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.collect
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

private const val URL = "/v1/product"

@IntegrationTest
class ProductV1ControllerTest(
    private val productDisplayRepository: ProductDisplayRepository,
    private val webTestClient: WebTestClient,
    private val productBrandRepository: ProductBrandRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productRepository: ProductRepository,
) : BehaviorSpec({
    afterEach {
        productDisplayRepository.deleteAll()
        productBrandRepository.deleteAll()
        productCategoryRepository.deleteAll()
        productRepository.deleteAll()
    }

    Given("page와 size를 통해") {
        val product1 = makeProductDisplay(
            productId = 1L,
            name = "name1",
            price = 20000,
            brand = "NIKE",
            category = "SHOES",
            lastBiddingId = 1L,
        )
        val product2 = makeProductDisplay(
            productId = 2L,
            name = "name2",
            price = 30000,
            brand = "MONCLER",
            category = "CLOTHES",
            lastBiddingId = 2L,
        )
        productDisplayRepository.saveAll(listOf(product1, product2)).collect()

        When("상품 전시 목록을 요청하면") {
            val response = webTestClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path(URL)
                        .queryParam("page", 0)
                        .queryParam("size", 1)
                        .build()
                }
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .exchange()

            Then("전시 목록이 리턴된다") {
                response.expectStatus().isOk
                    .expectBody()
                    .jsonPath("$.data[0].name").isEqualTo("name2")
                    .jsonPath("$.data[0].price").isEqualTo(30000)
                    .jsonPath("$.data[0].brand").isEqualTo("MONCLER")
                    .jsonPath("$.data[0].category").isEqualTo("CLOTHES")
            }
        }
    }

    Given("존재하지 않는 브랜드의") {
        val request = makeRegisterProductRequest()
        val exc = ProductBrandNotFoundException()

        When("상품 등록을 요청하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("404가 리턴된다") {
                response.expectStatus().isNotFound
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("존재하지 않는 카테고리의") {
        val productBrand = productBrandRepository.save(makeProductBrand())

        val request = makeRegisterProductRequest(brandId = productBrand.id)
        val exc = ProductCategoryNotFoundException()

        When("상품 등록을 요청하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("404가 리턴된다") {
                response.expectStatus().isNotFound
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("상품 가격이 0원 이하일 때") {
        val productBrand = productBrandRepository.save(makeProductBrand())

        val productCategory = productCategoryRepository.save(makeProductCategory())

        val request = makeRegisterProductRequest(releasePrice = 0, brandId = productBrand.id, categoryId = productCategory.id)
        val exc = InvalidReleasePriceException()

        When("상품 등록을 요청하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400이 리턴된다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
            }
        }
    }

    Given("존재하는 브랜드/카테고리를 대상으로") {
        val productBrand = productBrandRepository.save(makeProductBrand())

        val productCategory = productCategoryRepository.save(makeProductCategory())

        val request = makeRegisterProductRequest(
            releasePrice = 10000,
            brandId = productBrand.id,
            categoryId = productCategory.id,
        )

        When("상품 등록을 요청하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("상품이 정상 등록된다") {
                response.expectStatus().isOk
                    .expectBody()
                    .jsonPath("name").isEqualTo(request.name)
                    .jsonPath("releasePrice").isEqualTo(request.releasePrice)
                    .jsonPath("modelNumber").isEqualTo(request.modelNumber)
                    .jsonPath("sizeType").isEqualTo(request.sizeType.name)
                    .jsonPath("brand").isEqualTo(productBrand.name)
                    .jsonPath("category").isEqualTo(productCategory.name)

                productRepository.count() shouldBe 1
            }
        }
    }
})
