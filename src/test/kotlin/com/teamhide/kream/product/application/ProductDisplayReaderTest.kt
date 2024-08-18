package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.makeProductDisplay
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class ProductDisplayReaderTest : StringSpec({
    val productDisplayRepository = mockk<ProductDisplayRepository>()
    val productDisplayReader =
        ProductDisplayReader(productDisplayRepository = productDisplayRepository)

    "productId로 ProductDisplay를 조회한다" {
        // Given
        val productId = 1L
        val productDisplay = makeProductDisplay()
        coEvery { productDisplayRepository.findByProductId(any()) } returns productDisplay

        // When
        val sut = productDisplayReader.findByProductId(productId = productId)

        // Then
        sut.shouldNotBeNull()
        sut.productId shouldBe productDisplay.productId
        sut.name shouldBe productDisplay.name
        sut.price shouldBe productDisplay.price
        sut.brand shouldBe productDisplay.brand
        sut.category shouldBe productDisplay.category
    }

    "모든 ProductDisplay를 조회한다" {
        // Given
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
        coEvery { productDisplayRepository.findAllByOrderByIdDesc(any()) } returns flowOf(product1, product2)

        // When
        val sut = productDisplayReader.findAllBy(page = 0, size = 20)

        // Then
        sut.size shouldBe 2
        sut[0].productId shouldBe product1.productId
        sut[0].name shouldBe product1.name
        sut[0].price shouldBe product1.price
        sut[0].brand shouldBe product1.brand
        sut[0].category shouldBe product1.category

        sut[1].productId shouldBe product2.productId
        sut[1].name shouldBe product2.name
        sut[1].price shouldBe product2.price
        sut[1].brand shouldBe product2.brand
        sut[1].category shouldBe product2.category
    }
})
