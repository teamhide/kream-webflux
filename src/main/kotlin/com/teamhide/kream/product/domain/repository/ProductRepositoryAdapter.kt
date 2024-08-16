package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductBrand
import com.teamhide.kream.product.domain.model.ProductCategory
import com.teamhide.kream.product.domain.model.ProductInfo
import org.springframework.stereotype.Component

@Component
class ProductRepositoryAdapter(
    private val productRepository: ProductRepository,
    private val productBrandRepository: ProductBrandRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) {
    suspend fun findById(productId: Long): Product? {
        return productRepository.findById(id = productId)
    }

    suspend fun saveProduct(product: Product): Product {
        return productRepository.save(product)
    }

    suspend fun findCategoryById(categoryId: Long): ProductCategory? {
        return productCategoryRepository.findById(categoryId)
    }

    suspend fun findBrandById(brandId: Long): ProductBrand? {
        return productBrandRepository.findById(brandId)
    }

    suspend fun findInfoById(productId: Long): ProductInfo? {
        val product = productRepository.findInfoById(productId = productId) ?: return null
        return product.let {
            ProductInfo(
                productId = it.productId,
                releasePrice = it.releasePrice,
                modelNumber = it.modelNumber,
                name = it.name,
                brand = it.brand,
                category = it.category,
            )
        }
    }
}
