package com.teamhide.kream.product.ui.api.dto

import com.teamhide.kream.product.domain.model.ProductDetail

data class GetProductResponse(
    val productId: Long,
    val releasePrice: Int,
    val modelNumber: String,
    val name: String,
    val brand: String,
    val category: String,
    val purchaseBidPrice: Int?,
    val saleBidPrice: Int?,
) {
    companion object {
        fun from(productDetail: ProductDetail): GetProductResponse {
            return productDetail.let {
                GetProductResponse(
                    productId = it.productId,
                    releasePrice = it.releasePrice,
                    modelNumber = it.modelNumber,
                    name = it.name,
                    brand = it.brand,
                    category = it.category,
                    purchaseBidPrice = it.purchaseBidPrice,
                    saleBidPrice = it.saleBidPrice,
                )
            }
        }
    }
}
