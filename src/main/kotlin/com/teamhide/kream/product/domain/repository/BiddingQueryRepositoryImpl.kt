package com.teamhide.kream.product.domain.repository

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.model.BiddingType
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Sort.Order.asc
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query

class BiddingQueryRepositoryImpl(
    private val entityTemplate: R2dbcEntityTemplate,
) : BiddingQueryRepository {
    override suspend fun findMostExpensiveBidding(productId: Long, biddingType: BiddingType): Bidding? {
        val query = query(
            where("productId").`is`(productId)
                .and("biddingType").`is`(biddingType)
                .and("status").`is`(BiddingStatus.IN_PROGRESS)
        ).sort(by(desc("price"))).limit(1)
        return entityTemplate
            .select(query, Bidding::class.java)
            .singleOrEmpty()
            .awaitSingleOrNull()
    }

    override suspend fun findMostCheapestBidding(productId: Long, biddingType: BiddingType): Bidding? {
        val query = query(
            where("productId").`is`(productId)
                .and("biddingType").`is`(biddingType)
                .and("status").`is`(BiddingStatus.IN_PROGRESS)
        ).sort(by(asc("price"))).limit(1)
        return entityTemplate
            .select(query, Bidding::class.java)
            .singleOrEmpty()
            .awaitSingleOrNull()
    }
}
