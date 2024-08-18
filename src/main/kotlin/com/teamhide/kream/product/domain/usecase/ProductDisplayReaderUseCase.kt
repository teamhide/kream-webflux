package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.ProductDisplay

interface ProductDisplayReaderUseCase {
    suspend fun findByProductId(productId: Long): ProductDisplay?
    suspend fun findAllBy(page: Int, size: Int): List<ProductDisplay>
}
