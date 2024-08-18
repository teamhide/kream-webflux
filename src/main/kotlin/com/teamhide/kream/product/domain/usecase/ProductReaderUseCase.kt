package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductBrand
import com.teamhide.kream.product.domain.model.ProductCategory
import com.teamhide.kream.product.domain.model.ProductInfo

interface ProductReaderUseCase {
    suspend fun findBrandById(brandId: Long): ProductBrand?
    suspend fun findCategoryById(categoryId: Long): ProductCategory?
    suspend fun findProductInfoById(productId: Long): ProductInfo?
    suspend fun findById(productId: Long): Product?
}
