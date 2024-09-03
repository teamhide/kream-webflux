package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.ProductBrandNotFoundException
import com.teamhide.kream.product.application.exception.ProductCategoryNotFoundException
import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.domain.repository.ProductRepository
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import com.teamhide.kream.product.domain.usecase.RegisterProductCommand
import com.teamhide.kream.product.domain.usecase.RegisterProductResponseDto
import com.teamhide.kream.product.domain.usecase.RegisterProductUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class RegisterProductService(
    private val productReaderUseCase: ProductReaderUseCase,
    private val productRepository: ProductRepository,
    private val productDisplayRepository: ProductDisplayRepository,
) : RegisterProductUseCase {
    override suspend fun registerProduct(command: RegisterProductCommand): RegisterProductResponseDto = coroutineScope {
        val productBrandDeferred = async {
            productReaderUseCase.findBrandById(command.brandId) ?: throw ProductBrandNotFoundException()
        }
        val productCategoryDeferred = async {
            productReaderUseCase.findCategoryById(command.categoryId) ?: throw ProductCategoryNotFoundException()
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
        val savedProduct = productRepository.save(product).let {
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
        productDisplayRepository.save(productDisplay)
        savedProduct
    }
}
