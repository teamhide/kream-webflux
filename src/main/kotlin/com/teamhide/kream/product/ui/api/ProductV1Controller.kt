package com.teamhide.kream.product.ui.api

import com.teamhide.kream.common.response.ApiResponse
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.domain.usecase.GetProductDetailQuery
import com.teamhide.kream.product.domain.usecase.ProductFinderUseCase
import com.teamhide.kream.product.domain.usecase.RegisterProductUseCase
import com.teamhide.kream.product.ui.api.dto.GetProductResponse
import com.teamhide.kream.product.ui.api.dto.GetProductsResponse
import com.teamhide.kream.product.ui.api.dto.RegisterProductRequest
import com.teamhide.kream.product.ui.api.dto.RegisterProductResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/product")
class ProductV1Controller(
    private val productFinderUseCase: ProductFinderUseCase,
    private val registerProductUseCase: RegisterProductUseCase,
) {
    @GetMapping("")
    suspend fun getProducts(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
    ): ApiResponse<GetProductsResponse> {
        val query = GetAllProductQuery(page = page, size = size)
        val products = productFinderUseCase.getAllProducts(query = query)
        return ApiResponse.success(body = GetProductsResponse(data = products), statusCode = HttpStatus.OK)
    }

    @PostMapping("")
    suspend fun registerProduct(@RequestBody @Valid body: RegisterProductRequest): ApiResponse<RegisterProductResponse> {
        val command = body.toCommand()
        val responseDto = registerProductUseCase.registerProduct(command = command)
        return ApiResponse.success(body = RegisterProductResponse.from(responseDto), statusCode = HttpStatus.OK)
    }

    @GetMapping("/{productId}")
    suspend fun getProductDetail(@PathVariable("productId") productId: Long): ApiResponse<GetProductResponse> {
        val query = GetProductDetailQuery(productId = productId)
        val productDetail = productFinderUseCase.getDetailById(query = query)
        val response = GetProductResponse.from(productDetail)
        return ApiResponse.success(body = response, statusCode = HttpStatus.OK)
    }
}
