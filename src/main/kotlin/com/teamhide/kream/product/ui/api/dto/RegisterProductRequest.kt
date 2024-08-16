package com.teamhide.kream.product.ui.api.dto

import com.teamhide.kream.product.domain.model.SizeType
import com.teamhide.kream.product.domain.usecase.RegisterProductCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RegisterProductRequest(
    @field:NotBlank
    val name: String,

    @field:NotNull
    val releasePrice: Int,

    @field:NotBlank
    val modelNumber: String,

    @field:NotNull
    val sizeType: SizeType,

    @field:NotNull
    val brandId: Long,

    @field:NotNull
    val categoryId: Long,
) {
    fun toCommand(): RegisterProductCommand {
        return this.let {
            RegisterProductCommand(
                name = it.name,
                releasePrice = it.releasePrice,
                modelNumber = it.modelNumber,
                sizeType = it.sizeType,
                brandId = it.brandId,
                categoryId = it.categoryId,
            )
        }
    }
}
