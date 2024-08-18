package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Product
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductRepository : CoroutineCrudRepository<Product, Long>, ProductQueryRepository
