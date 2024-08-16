package com.teamhide.kream.product.domain.usecase

import com.teamhide.kream.product.domain.model.SizeType

data class RegisterProductCommand(
    val name: String,
    val releasePrice: Int,
    val modelNumber: String,
    val sizeType: SizeType,
    val brandId: Long,
    val categoryId: Long,
)

data class RegisterProductResponseDto(
    val id: Long,
    val name: String,
    val releasePrice: Int,
    val modelNumber: String,
    val sizeType: SizeType,
    val brand: String,
    val category: String,
)

interface RegisterProductUseCase {
    suspend fun registerProduct(command: RegisterProductCommand): RegisterProductResponseDto
}
