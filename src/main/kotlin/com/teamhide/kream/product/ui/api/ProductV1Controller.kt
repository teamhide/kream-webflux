package com.teamhide.kream.product.ui.api

import com.teamhide.kream.common.response.ApiResponse
import com.teamhide.kream.product.application.service.ProductCommandService
import com.teamhide.kream.product.application.service.ProductQueryService
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.domain.usecase.GetProductDetailQuery
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
    private val productQueryService: ProductQueryService,
    private val productCommandService: ProductCommandService,
) {
    @GetMapping("")
    suspend fun getProducts(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int,
    ): ApiResponse<GetProductsResponse> {
        val query = GetAllProductQuery(page = page, size = size)
        val products = productQueryService.getAllProducts(query = query)
        return ApiResponse.success(body = GetProductsResponse(data = products), statusCode = HttpStatus.OK)
    }

    @PostMapping("")
    suspend fun registerProduct(@RequestBody @Valid body: RegisterProductRequest): ApiResponse<RegisterProductResponse> {
        val command = body.toCommand()
        val responseDto = productCommandService.registerProduct(command = command)
        return ApiResponse.success(body = RegisterProductResponse.from(responseDto), statusCode = HttpStatus.OK)
    }

    @GetMapping("/{productId}")
    suspend fun getProductDetail(@PathVariable("productId") productId: Long): ApiResponse<GetProductResponse> {
        val query = GetProductDetailQuery(productId = productId)
        val productDetail = productQueryService.getDetailById(query = query)
        val response = GetProductResponse.from(productDetail)
        return ApiResponse.success(body = response, statusCode = HttpStatus.OK)
    }
}
