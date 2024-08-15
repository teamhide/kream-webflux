package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.ProductBrand
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ProductBrandRepository : CoroutineCrudRepository<ProductBrand, Long>
