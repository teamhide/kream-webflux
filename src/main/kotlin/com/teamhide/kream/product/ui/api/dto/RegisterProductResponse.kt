package com.teamhide.kream.product.ui.api.dto

import com.teamhide.kream.product.domain.model.SizeType
import com.teamhide.kream.product.domain.usecase.RegisterProductResponseDto

data class RegisterProductResponse(
    val id: Long,
    val name: String,
    val releasePrice: Int,
    val modelNumber: String,
    val sizeType: SizeType,
    val brand: String,
    val category: String,
) {
    companion object {
        fun from(responseDto: RegisterProductResponseDto): RegisterProductResponse {
            return responseDto.let {
                RegisterProductResponse(
                    id = it.id,
                    name = it.name,
                    releasePrice = it.releasePrice,
                    modelNumber = it.modelNumber,
                    sizeType = it.sizeType,
                    brand = it.brand,
                    category = it.category,
                )
            }
        }
    }
}
