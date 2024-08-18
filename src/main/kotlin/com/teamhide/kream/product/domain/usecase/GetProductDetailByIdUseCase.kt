package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.ProductDetail

data class GetProductDetailQuery(val productId: Long)

interface GetProductDetailByIdUseCase {
    suspend fun getDetailById(query: GetProductDetailQuery): ProductDetail
}
