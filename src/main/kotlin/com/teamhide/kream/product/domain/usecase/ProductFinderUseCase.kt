package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.ProductDetail
import com.teamhide.kream.product.domain.model.ProductDisplayRead

data class GetAllProductQuery(val page: Int, val size: Int)
data class GetProductDetailQuery(val productId: Long)

interface ProductFinderUseCase {
    suspend fun getAllProducts(query: GetAllProductQuery): List<ProductDisplayRead>
    suspend fun getDetailById(query: GetProductDetailQuery): ProductDetail
}
