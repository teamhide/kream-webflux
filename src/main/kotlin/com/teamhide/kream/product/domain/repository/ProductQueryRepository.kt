package com.teamhide.kream.product.domain.repository

interface ProductQueryRepository {
    suspend fun findInfoById(productId: Long): ProductInfoDto?
}
