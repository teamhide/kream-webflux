package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductBrand
import com.teamhide.kream.product.domain.model.ProductCategory
import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.model.ProductInfo

interface ProductReaderUseCase {
    suspend fun findBrandById(brandId: Long): ProductBrand?
    suspend fun findCategoryById(categoryId: Long): ProductCategory?
    suspend fun findProductInfoById(productId: Long): ProductInfo?
    suspend fun findProductById(productId: Long): Product?
    suspend fun findDisplayByProductId(productId: Long): ProductDisplay?
    suspend fun findDisplayAllBy(page: Int, size: Int): List<ProductDisplay>
}
