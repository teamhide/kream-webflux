package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.ProductNotFoundException
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.model.ProductDetail
import com.teamhide.kream.product.domain.model.ProductDisplayRead
import com.teamhide.kream.product.domain.usecase.BiddingReaderUseCase
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.domain.usecase.GetProductDetailQuery
import com.teamhide.kream.product.domain.usecase.ProductFinderUseCase
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.stereotype.Service

@Service
class ProductFinder(
    private val biddingReaderUseCase: BiddingReaderUseCase,
    private val productReaderUseCase: ProductReaderUseCase,
) : ProductFinderUseCase {
    override suspend fun getAllProducts(query: GetAllProductQuery): List<ProductDisplayRead> {
        return productReaderUseCase.findDisplayAllBy(page = query.page, size = query.size)
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

    override suspend fun getDetailById(query: GetProductDetailQuery): ProductDetail {
        val product = productReaderUseCase.findProductInfoById(productId = query.productId)
            ?: throw ProductNotFoundException()

        val expensiveBiddingDeferred = CoroutineScope(Dispatchers.IO).async {
            biddingReaderUseCase.findMostExpensiveBidding(
                productId = product.productId,
                biddingType = BiddingType.PURCHASE
            )
        }
        val cheapestBiddingDeferred = CoroutineScope(Dispatchers.IO).async {
            biddingReaderUseCase.findMostCheapestBidding(
                productId = product.productId,
                biddingType = BiddingType.SALE
            )
        }
        val expensiveBidding = expensiveBiddingDeferred.await()
        val cheapestBidding = cheapestBiddingDeferred.await()

        return ProductDetail(
            productId = product.productId,
            releasePrice = product.releasePrice,
            modelNumber = product.modelNumber,
            name = product.name,
            brand = product.brand,
            category = product.category,
            purchaseBidPrice = cheapestBidding?.price,
            saleBidPrice = expensiveBidding?.price,
        )
    }
}
