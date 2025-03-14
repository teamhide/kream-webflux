package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductBrand
import com.teamhide.kream.product.domain.model.ProductCategory
import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.model.ProductInfo
import com.teamhide.kream.product.domain.repository.ProductBrandRepository
import com.teamhide.kream.product.domain.repository.ProductCategoryRepository
import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.domain.repository.ProductRepository
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductReader(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val productBrandRepository: ProductBrandRepository,
    private val productDisplayRepository: ProductDisplayRepository,
) : ProductReaderUseCase {
    override suspend fun findBrandById(brandId: Long): ProductBrand? {
        return productBrandRepository.findById(brandId)
    }

    override suspend fun findCategoryById(categoryId: Long): ProductCategory? {
        return productCategoryRepository.findById(categoryId)
    }

    override suspend fun findProductInfoById(productId: Long): ProductInfo? {
        val product = productRepository.findInfoById(productId = productId) ?: return null
        return product.let {
            ProductInfo(
                productId = it.productId,
                releasePrice = it.releasePrice,
                modelNumber = it.modelNumber,
                name = it.name,
                brand = it.brand,
                category = it.category,
            )
        }
    }

    override suspend fun findProductById(productId: Long): Product? {
        return productRepository.findById(id = productId)
    }

    override suspend fun findDisplayByProductId(productId: Long): ProductDisplay? {
        return productDisplayRepository.findByProductId(productId = productId)
    }

    override suspend fun findDisplayAllBy(page: Int, size: Int): List<ProductDisplay> {
        val pageRequest = PageRequest.of(page, size)
        return productDisplayRepository.findAllByOrderByIdDesc(pageable = pageRequest).toList()
    }
}
