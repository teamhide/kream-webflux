package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.repository.ProductBrandRepository
import com.teamhide.kream.product.domain.repository.ProductCategoryRepository
import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.domain.repository.ProductRepository
import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.product.makeProductBrand
import com.teamhide.kream.product.makeProductCategory
import com.teamhide.kream.product.makeProductDisplay
import com.teamhide.kream.product.makeProductInfoDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class ProductReaderTest : StringSpec({
    val productRepository = mockk<ProductRepository>()
    val productCategoryRepository = mockk<ProductCategoryRepository>()
    val productBrandRepository = mockk<ProductBrandRepository>()
    val productDisplayRepository = mockk<ProductDisplayRepository>()
    val productReader = ProductReader(
        productRepository = productRepository,
        productBrandRepository = productBrandRepository,
        productCategoryRepository = productCategoryRepository,
        productDisplayRepository = productDisplayRepository,
    )

    "id로 Product를 조회한다" {
        // Given
        val id = 1L
        val product = makeProduct()
        coEvery { productRepository.findById(id) } returns product

        // When
        val sut = productReader.findProductById(id)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe product.id
        sut.name shouldBe product.name
        sut.productCategoryId shouldBe product.productCategoryId
        sut.productBrandId shouldBe product.productBrandId
        sut.releasePrice shouldBe product.releasePrice
        sut.modelNumber shouldBe product.modelNumber
        sut.sizeType shouldBe product.sizeType
    }

    "id로 ProductCategory를 조회한다" {
        // Given
        val id = 1L
        val productCategory = makeProductCategory()
        coEvery { productReader.findCategoryById(id) } returns productCategory

        // When
        val sut = productReader.findCategoryById(id)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe productCategory.id
        sut.name shouldBe productCategory.name
        sut.parentCategoryId shouldBe productCategory.parentCategoryId
    }

    "id로 ProductBrand를 조회한다" {
        // Given
        val id = 1L
        val productBrand = makeProductBrand()
        coEvery { productReader.findBrandById(id) } returns productBrand

        // When
        val sut = productReader.findBrandById(id)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe productBrand.id
        sut.name shouldBe productBrand.name
    }

    "id로 ProductInfo를 조회한다 - 존재하지 않는 경우" {
        // Given
        coEvery { productRepository.findInfoById(any()) } returns null

        // When
        val sut = productReader.findProductInfoById(productId = 1L)

        // Then
        sut shouldBe null
    }

    "id로 ProductInfo를 조회한다" {
        // Given
        val productDetailDto = makeProductInfoDto()
        coEvery { productRepository.findInfoById(any()) } returns productDetailDto

        // When
        val sut = productReader.findProductInfoById(productId = 1L)

        // Then
        sut.shouldNotBeNull()
        sut.productId shouldBe productDetailDto.productId
        sut.releasePrice shouldBe productDetailDto.releasePrice
        sut.modelNumber shouldBe productDetailDto.modelNumber
        sut.name shouldBe productDetailDto.name
        sut.brand shouldBe productDetailDto.brand
        sut.category shouldBe productDetailDto.category
    }

    "productId로 ProductDisplay를 조회한다" {
        // Given
        val productId = 1L
        val productDisplay = makeProductDisplay()
        coEvery { productDisplayRepository.findByProductId(any()) } returns productDisplay

        // When
        val sut = productReader.findDisplayByProductId(productId = productId)

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
        val sut = productReader.findDisplayAllBy(page = 0, size = 20)

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
