package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.ProductDisplay
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class ProductDisplayRepositoryAdapter(
    private val productDisplayRepository: ProductDisplayRepository,
) {
    suspend fun findByProductId(productId: Long): ProductDisplay? {
        return productDisplayRepository.findByProductId(productId = productId)
    }

    suspend fun save(productDisplay: ProductDisplay): ProductDisplay {
        return productDisplayRepository.save(productDisplay)
    }

    suspend fun findAllBy(page: Int, size: Int): List<ProductDisplay> {
        val pageRequest = PageRequest.of(page, size)
        return productDisplayRepository.findAllByOrderByIdDesc(pageable = pageRequest).toList()
    }
}
