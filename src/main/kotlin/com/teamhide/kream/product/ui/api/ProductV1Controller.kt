package com.teamhide.kream.product.ui.api

import com.teamhide.kream.common.response.ApiResponse
import com.teamhide.kream.product.application.service.ProductQueryService
import com.teamhide.kream.product.domain.usecase.GetAllProductQuery
import com.teamhide.kream.product.ui.api.dto.GetProductsResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/product")
class ProductV1Controller(
    private val productQueryService: ProductQueryService,
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
}
