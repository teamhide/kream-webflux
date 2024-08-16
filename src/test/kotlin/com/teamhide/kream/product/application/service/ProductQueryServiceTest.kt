package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.domain.repository.ProductDisplayRepositoryAdapter
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.makeProductDisplay
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class ProductQueryServiceTest : BehaviorSpec({
    val productDisplayRepositoryAdapter = mockk<ProductDisplayRepositoryAdapter>()
    val productQueryService = ProductQueryService(productDisplayRepositoryAdapter = productDisplayRepositoryAdapter)

    Given("page와 size를 통해") {
        val query = GetAllProductQuery(page = 0, size = 20)
        val productDisplay1 = makeProductDisplay(productId = 1L)
        val productDisplay2 = makeProductDisplay(productId = 2L)
        coEvery { productDisplayRepositoryAdapter.findAllBy(any(), any()) } returns listOf(productDisplay1, productDisplay2)

        When("상품 전시 목록을 요청하면") {
            val sut = productQueryService.getAllProducts(query = query)

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
})
