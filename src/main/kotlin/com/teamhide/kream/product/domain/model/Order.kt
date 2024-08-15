package com.teamhide.kream.product.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
data class Order(
    @Column("payment_id")
    val paymentId: String,

    @Column("bidding_id")
    val biddingId: Long,

    @Column("user_id")
    val userId: Long,

    @Column("status")
    val status: OrderStatus,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity()
