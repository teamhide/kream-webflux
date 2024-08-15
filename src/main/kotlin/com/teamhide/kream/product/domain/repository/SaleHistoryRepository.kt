package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.SaleHistory
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface SaleHistoryRepository : CoroutineCrudRepository<SaleHistory, Long>
