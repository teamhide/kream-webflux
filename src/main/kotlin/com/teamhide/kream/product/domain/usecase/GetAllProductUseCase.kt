package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.ProductDisplayRead

data class GetAllProductQuery(val page: Int, val size: Int)

interface GetAllProductUseCase {
    suspend fun getAllProducts(query: GetAllProductQuery): List<ProductDisplayRead>
}
