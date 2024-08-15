package com.teamhide.kream.product.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product_category")
data class ProductCategory(
    @Column("name")
    val name: String,

    @Column("parent_category_id")
    val parentCategoryId: Long?,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity()
