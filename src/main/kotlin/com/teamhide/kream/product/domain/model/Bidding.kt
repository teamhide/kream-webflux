package com.teamhide.kream.product.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("bidding")
data class Bidding(
    @Column("product_id")
    val productId: Long,

    @Column("user_id")
    val userId: Long,

    @Column("price")
    val price: Int,

    @Column("size")
    val size: String,

    @Column("status")
    var status: BiddingStatus,

    @Column("bidding_type")
    val biddingType: BiddingType,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity() {
    init {
        if (price <= 0) {
            throw InvalidBiddingPriceException()
        }
    }

    fun changeStatus(status: BiddingStatus) {
        this.status = status
    }

    fun canBid(): Boolean {
        return this.status == BiddingStatus.IN_PROGRESS
    }
}
