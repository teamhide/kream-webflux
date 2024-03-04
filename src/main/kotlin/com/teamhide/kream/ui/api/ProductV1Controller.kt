package com.teamhide.kream.ui.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class CreateProductRequest(val name: String)

data class CreateProductResponse(val name: String)

@RestController
class ProductV1Controller {
    @GetMapping("/v1/product")
    suspend fun getProducts(): ResponseEntity<String> {
        return ResponseEntity.ok("abc")
    }

    @PostMapping("/v1/product")
    suspend fun createProduct(@RequestBody body: CreateProductRequest): CreateProductResponse {
        return CreateProductResponse(name = "hide")
    }
}
