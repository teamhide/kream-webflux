package com.teamhide.kream.product.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sale_history")
data class SaleHistory(
    @Column("bidding_id")
    val biddingId: Long,

    @Column("user_id")
    val userId: Long,

    @Column("price")
    val price: Int,

    @Column("size")
    val size: String,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity()
