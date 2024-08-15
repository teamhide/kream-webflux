package com.teamhide.kream.product.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product")
data class Product(
    @Column("name")
    val name: String,

    @Column("release_price")
    val releasePrice: Int,

    @Column("model_number")
    val modelNumber: String,

    @Column("size_type")
    val sizeType: SizeType,

    @Column("product_brand_id")
    val productBrandId: Long,

    @Column("product_category_id")
    val productCategoryId: Long,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity() {
    init {
        if (releasePrice <= 0) {
            throw InvalidReleasePriceException()
        }
    }
}
