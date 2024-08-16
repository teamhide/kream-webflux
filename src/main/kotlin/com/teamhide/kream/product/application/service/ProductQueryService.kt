package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.domain.model.ProductDisplayRead
import com.teamhide.kream.product.domain.repository.ProductDisplayRepositoryAdapter
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.domain.usecase.GetAllProductUseCase
import org.springframework.stereotype.Service

@Service
class ProductQueryService(
    private val productDisplayRepositoryAdapter: ProductDisplayRepositoryAdapter,
) : GetAllProductUseCase {
    override suspend fun getAllProducts(query: GetAllProductQuery): List<ProductDisplayRead> {
        return productDisplayRepositoryAdapter.findAllBy(page = query.page, size = query.size)
            .map {
                ProductDisplayRead(
                    productId = it.productId,
                    name = it.name,
                    price = it.price,
                    brand = it.brand,
                    category = it.category,
                )
            }
    }
}
