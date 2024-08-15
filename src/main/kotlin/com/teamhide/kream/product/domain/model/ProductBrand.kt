package com.teamhide.kream.product.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "product_brand")
data class ProductBrand(
    @Column("name")
    val name: String,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity()
