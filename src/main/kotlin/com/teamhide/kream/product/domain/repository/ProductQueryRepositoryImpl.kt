package com.teamhide.kream.product.domain.repository

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

class ProductQueryRepositoryImpl(
    private val entityTemplate: R2dbcEntityTemplate,
) : ProductQueryRepository {
    override suspend fun findInfoById(productId: Long): ProductInfoDto? {
        val sql = """
            SELECT 
                p.id AS productId, 
                p.release_price AS releasePrice, 
                p.model_number AS modelNumber, 
                p.name AS name, 
                b.name AS brand, 
                c.name AS category
            FROM product p
            INNER JOIN product_brand b ON p.product_brand_id = b.id
            INNER JOIN product_category c ON p.product_category_id = c.id
            WHERE p.id = :productId
        """
        return entityTemplate.databaseClient.sql(sql)
            .bind("productId", productId)
            .map { row, _ ->
                ProductInfoDto(
                    productId = row.get("productId", Long::class.java)!!,
                    releasePrice = row.get("releasePrice", Int::class.java)!!,
                    modelNumber = row.get("modelNumber", String::class.java)!!,
                    name = row.get("name", String::class.java)!!,
                    brand = row.get("brand", String::class.java)!!,
                    category = row.get("category", String::class.java)!!
                )
            }
            .first()
            .awaitSingleOrNull()
    }
}
