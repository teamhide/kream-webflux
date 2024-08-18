package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.repository.ProductDisplayRepositoryAdapter
import com.teamhide.kream.product.domain.repository.ProductRepositoryAdapter
import com.teamhide.kream.product.domain.usecase.SaveOrUpdateProductDisplayCommand
import com.teamhide.kream.product.domain.usecase.SaveOrUpdateProductDisplayUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SaveOrUpdateProductDisplayService(
    private val productDisplayRepositoryAdapter: ProductDisplayRepositoryAdapter,
    private val productRepositoryAdapter: ProductRepositoryAdapter,
) : SaveOrUpdateProductDisplayUseCase {
    override suspend fun execute(command: SaveOrUpdateProductDisplayCommand) {
        val existingProductDisplay = productDisplayRepositoryAdapter.findByProductId(productId = command.productId)

        if (existingProductDisplay == null) {
            handleNewProductDisplay(command = command)
        } else {
            handleExistingProductDisplay(command = command, existingProductDisplay = existingProductDisplay)
        }
    }

    private suspend fun handleNewProductDisplay(command: SaveOrUpdateProductDisplayCommand) = coroutineScope {
        val product = productRepositoryAdapter.findById(productId = command.productId) ?: return@coroutineScope

        val brandDeferred = async {
            productRepositoryAdapter.findBrandById(brandId = product.productBrandId)
        }
        val categoryDeferred = async {
            productRepositoryAdapter.findCategoryById(categoryId = product.productCategoryId)
        }
        val brand = brandDeferred.await()
        val category = categoryDeferred.await()
        if (brand == null || category == null) return@coroutineScope

        val productDisplay = ProductDisplay(
            productId = product.id,
            name = product.name,
            price = command.price,
            brand = brand.name,
            category = category.name,
            lastBiddingId = command.biddingId
        )
        productDisplayRepositoryAdapter.save(productDisplay)
    }

    private suspend fun handleExistingProductDisplay(
        command: SaveOrUpdateProductDisplayCommand,
        existingProductDisplay: ProductDisplay,
    ) {
        if (existingProductDisplay.price != 0 && existingProductDisplay.price < command.price) {
            return
        }
        existingProductDisplay.changePrice(command.price)
        existingProductDisplay.changeLastBiddingId(command.biddingId)
        productDisplayRepositoryAdapter.save(existingProductDisplay)
    }
}
