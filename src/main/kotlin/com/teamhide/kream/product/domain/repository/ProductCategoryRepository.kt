package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.ProductCategory
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductCategoryRepository : CoroutineCrudRepository<ProductCategory, Long>
