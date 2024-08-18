package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.product.makeProductBrand
import com.teamhide.kream.product.makeProductCategory
import com.teamhide.kream.support.test.IntegrationTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

@IntegrationTest
class ProductQueryRepositoryImplTest(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productBrandRepository: ProductBrandRepository,
) : StringSpec({
    afterEach {
        productRepository.deleteAll()
        productCategoryRepository.deleteAll()
        productBrandRepository.deleteAll()
    }

    "ID로 제품 정보 DTO를 조회한다" {
        // Given
        val category = productCategoryRepository.save(makeProductCategory())
        val brand = productBrandRepository.save(makeProductBrand())
        val product = productRepository.save(makeProduct(productBrandId = brand.id, productCategoryId = category.id))

        // When
        val productInfo = productRepository.findInfoById(productId = product.id)

        // Then
        productInfo.shouldNotBeNull()
        productInfo.productId shouldBe product.id
        productInfo.releasePrice shouldBe product.releasePrice
        productInfo.modelNumber shouldBe product.modelNumber
        productInfo.name shouldBe product.name
        productInfo.brand shouldBe brand.name
        productInfo.category shouldBe category.name
    }

    "ID로 "
})
