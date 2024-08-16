package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.application.exception.ProductBrandNotFoundException
import com.teamhide.kream.product.application.exception.ProductCategoryNotFoundException
import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.repository.ProductDisplayRepositoryAdapter
import com.teamhide.kream.product.domain.repository.ProductRepositoryAdapter
import com.teamhide.kream.product.domain.usecase.RegisterProductCommand
import com.teamhide.kream.product.domain.usecase.RegisterProductResponseDto
import com.teamhide.kream.product.domain.usecase.RegisterProductUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.stereotype.Service

@Service
class ProductCommandService(
    val productRepositoryAdapter: ProductRepositoryAdapter,
    val productDisplayRepositoryAdapter: ProductDisplayRepositoryAdapter,
) : RegisterProductUseCase {
    override suspend fun registerProduct(command: RegisterProductCommand): RegisterProductResponseDto {
        val productBrandDeferred = CoroutineScope(Dispatchers.IO).async {
            productRepositoryAdapter.findBrandById(command.brandId) ?: throw ProductBrandNotFoundException()
        }
        val productCategoryDeferred = CoroutineScope(Dispatchers.IO).async {
            productRepositoryAdapter.findCategoryById(command.categoryId) ?: throw ProductCategoryNotFoundException()
        }

        val productBrand = productBrandDeferred.await()
        val productCategory = productCategoryDeferred.await()

        val product = Product.create(
            name = command.name,
            releasePrice = command.releasePrice,
            modelNumber = command.modelNumber,
            sizeType = command.sizeType,
            productBrandId = productBrand.id,
            productCategoryId = productCategory.id,
        )
        val savedProduct = productRepositoryAdapter.saveProduct(product = product).let {
            RegisterProductResponseDto(
                id = it.id,
                name = it.name,
                releasePrice = it.releasePrice,
                modelNumber = it.modelNumber,
                sizeType = it.sizeType,
                brand = productBrand.name,
                category = productCategory.name,
            )
        }
        val productDisplay = ProductDisplay.create(
            productId = product.id,
            name = product.name,
            brand = productBrand.name,
            category = productCategory.name,
        )
        productDisplayRepositoryAdapter.save(productDisplay = productDisplay)
        return savedProduct
    }
}
