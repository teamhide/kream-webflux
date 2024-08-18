package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.domain.usecase.ProductDisplayReaderUseCase
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductDisplayReader(
    private val productDisplayRepository: ProductDisplayRepository,
) : ProductDisplayReaderUseCase {
    override suspend fun findByProductId(productId: Long): ProductDisplay? {
        return productDisplayRepository.findByProductId(productId = productId)
    }

    override suspend fun findAllBy(page: Int, size: Int): List<ProductDisplay> {
        val pageRequest = PageRequest.of(page, size)
        return productDisplayRepository.findAllByOrderByIdDesc(pageable = pageRequest).toList()
    }
}
