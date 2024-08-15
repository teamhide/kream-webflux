package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository : CoroutineCrudRepository<Order, Long>
