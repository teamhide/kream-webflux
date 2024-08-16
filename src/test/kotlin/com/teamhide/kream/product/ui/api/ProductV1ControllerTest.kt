package com.teamhide.kream.product.ui.api

import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.makeProductDisplay
import com.teamhide.kream.support.test.IntegrationTest
import com.teamhide.kream.user.USER_ID_1_TOKEN
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.flow.collect
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

private const val URL = "/api/v1/product"

@IntegrationTest
class ProductV1ControllerTest(
    private val productDisplayRepository: ProductDisplayRepository,
    private val webTestClient: WebTestClient,
) : BehaviorSpec({
    afterEach {
        productDisplayRepository.deleteAll()
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
})
