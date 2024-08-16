package com.teamhide.kream.product.domain.repository

data class ProductInfoDto(
    val productId: Long,
    val releasePrice: Int,
    val modelNumber: String,
    val name: String,
    val brand: String,
    val category: String,
)
