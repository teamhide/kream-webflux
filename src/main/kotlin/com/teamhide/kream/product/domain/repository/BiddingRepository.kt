package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Bidding
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface BiddingRepository : CoroutineCrudRepository<Bidding, Long>
