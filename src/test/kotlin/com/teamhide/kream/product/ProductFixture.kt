package com.teamhide.kream.product

import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductBrand
import com.teamhide.kream.product.domain.model.ProductCategory
import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.model.SizeType
import com.teamhide.kream.product.domain.repository.ProductInfoDto

fun makeProductCategory(
    id: Long = 1L,
    name: String = "categoryName",
    parentCategoryId: Long = 1L,
): ProductCategory {
    return ProductCategory(
        name = name,
        parentCategoryId = parentCategoryId,
        id = id,
    )
}

fun makeProductBrand(
    id: Long = 1L,
    name: String = "brandName",
): ProductBrand {
    return ProductBrand(id = id, name = name)
}

fun makeProduct(
    id: Long = 1L,
    name: String = "productName",
    releasePrice: Int = 1000,
    modelNumber: String = "A-123",
    sizeType: SizeType = SizeType.CLOTHES,
    productBrandId: Long = 1L,
    productCategoryId: Long = 1L,
): Product {
    return Product(
        id = id,
        name = name,
        releasePrice = releasePrice,
        modelNumber = modelNumber,
        sizeType = sizeType,
        productBrandId = productBrandId,
        productCategoryId = productCategoryId,
    )
}

fun makeProductInfoDto(
    productId: Long = 1L,
    releasePrice: Int = 20000,
    modelNumber: String = "A-123",
    name: String = "SAKAI",
    brand: String = "NIKE",
    category: String = "SHOES",
): ProductInfoDto {
    return ProductInfoDto(
        productId = productId,
        releasePrice = releasePrice,
        modelNumber = modelNumber,
        name = name,
        brand = brand,
        category = category,
    )
}

fun makeProductDisplay(
    productId: Long = 1L,
    name: String = "name",
    price: Int = 10000,
    brand: String = "Nike",
    category: String = "SHOES",
    lastBiddingId: Long = 1L,
): ProductDisplay {
    return ProductDisplay(
        productId = productId,
        name = name,
        price = price,
        brand = brand,
        category = category,
        lastBiddingId = lastBiddingId,
    )
}
