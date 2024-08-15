package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.ProductDisplay
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductDisplayRepository : CoroutineCrudRepository<ProductDisplay, Long> {
    suspend fun findByProductId(productId: Long): ProductDisplay?

    suspend fun findAllBy(pageable: Pageable): List<ProductDisplay>
}
