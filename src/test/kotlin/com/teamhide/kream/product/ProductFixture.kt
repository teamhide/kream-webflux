package com.teamhide.kream.product

import com.teamhide.kream.product.domain.model.Bidding
import com.teamhide.kream.product.domain.model.BiddingStatus
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.product.domain.model.Order
import com.teamhide.kream.product.domain.model.OrderStatus
import com.teamhide.kream.product.domain.model.Product
import com.teamhide.kream.product.domain.model.ProductBrand
import com.teamhide.kream.product.domain.model.ProductCategory
import com.teamhide.kream.product.domain.model.ProductDisplay
import com.teamhide.kream.product.domain.model.ProductInfo
import com.teamhide.kream.product.domain.model.SaleHistory
import com.teamhide.kream.product.domain.model.SizeType
import com.teamhide.kream.product.domain.repository.ProductInfoDto
import com.teamhide.kream.product.domain.usecase.BidCommand
import com.teamhide.kream.product.domain.usecase.ImmediatePurchaseCommand
import com.teamhide.kream.product.domain.usecase.RegisterProductCommand
import com.teamhide.kream.product.domain.usecase.SaveOrUpdateProductDisplayCommand
import com.teamhide.kream.product.ui.api.dto.BidRequest
import com.teamhide.kream.product.ui.api.dto.RegisterProductRequest

fun makeProductCategory(
    id: Long = 0L,
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
    id: Long = 0L,
    name: String = "brandName",
): ProductBrand {
    return ProductBrand(id = id, name = name)
}

fun makeProduct(
    id: Long = 0L,
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
    productId: Long = 0L,
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
    productId: Long = 0L,
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

fun makeRegisterProductCommand(
    name: String = "name",
    releasePrice: Int = 1000,
    modelNumber: String = "A-123",
    sizeType: SizeType = SizeType.CLOTHES,
    brandId: Long = 1L,
    categoryId: Long = 1L,
): RegisterProductCommand {
    return RegisterProductCommand(
        name = name,
        releasePrice = releasePrice,
        modelNumber = modelNumber,
        sizeType = sizeType,
        brandId = brandId,
        categoryId = categoryId,
    )
}

fun makeRegisterProductRequest(
    name: String = "CLUNY",
    releasePrice: Int = 59000,
    modelNumber: String = "B-123",
    sizeType: SizeType = SizeType.CLOTHES,
    brandId: Long = 1L,
    categoryId: Long = 1L,
): RegisterProductRequest {
    return RegisterProductRequest(
        name = name,
        releasePrice = releasePrice,
        modelNumber = modelNumber,
        sizeType = sizeType,
        brandId = brandId,
        categoryId = categoryId,
    )
}

fun makeBidding(
    id: Long = 0L,
    productId: Long = 1L,
    userId: Long = 1L,
    price: Int = 20000,
    size: String = "M",
    status: BiddingStatus = BiddingStatus.IN_PROGRESS,
    biddingType: BiddingType = BiddingType.SALE,
): Bidding {
    return Bidding(
        id = id,
        productId = productId,
        userId = userId,
        price = price,
        size = size,
        status = status,
        biddingType = biddingType,
    )
}

fun makeSaleHistory(
    biddingId: Long = 1L,
    userId: Long = 1L,
    price: Int = 20000,
    size: String = "M"
): SaleHistory {
    return SaleHistory(
        biddingId = biddingId,
        userId = userId,
        price = price,
        size = size,
    )
}

fun makeOrder(
    id: Long = 0L,
    paymentId: String = "paymentId",
    biddingId: Long = 1L,
    userId: Long = 1L,
    status: OrderStatus = OrderStatus.COMPLETE,
): Order {
    return Order(
        id = id,
        paymentId = paymentId,
        biddingId = biddingId,
        userId = userId,
        status = status,
    )
}

fun makeProductInfo(
    productId: Long = 1L,
    releasePrice: Int = 20000,
    modelNumber: String = "A-123",
    name: String = "name",
    brand: String = "NIKE",
    category: String = "SHOES",
): ProductInfo {
    return ProductInfo(
        productId = productId,
        releasePrice = releasePrice,
        modelNumber = modelNumber,
        name = name,
        brand = brand,
        category = category,
    )
}

fun makeBidCommand(
    productId: Long = 1L,
    price: Int = 1000,
    size: String = "M",
    biddingType: BiddingType = BiddingType.PURCHASE,
    userId: Long = 1L,
): BidCommand {
    return BidCommand(
        productId = productId,
        price = price,
        size = size,
        biddingType = biddingType,
        userId = userId,
    )
}

fun makeBidRequest(
    productId: Long = 1L,
    price: Int = 50000,
    size: String = "M",
    biddingType: BiddingType = BiddingType.SALE,
): BidRequest {
    return BidRequest(
        productId = productId,
        price = price,
        size = size,
        biddingType = biddingType,
    )
}

fun makeSaveOrUpdateProductDisplayCommand(
    productId: Long = 1L,
    price: Int = 20000,
    biddingId: Long = 1L,
): SaveOrUpdateProductDisplayCommand {
    return SaveOrUpdateProductDisplayCommand(
        productId = productId,
        price = price,
        biddingId = biddingId,
    )
}

fun makeImmediatePurchaseCommand(
    biddingId: Long = 1L,
    userId: Long = 1L,
): ImmediatePurchaseCommand {
    return ImmediatePurchaseCommand(
        biddingId = biddingId,
        userId = userId,
    )
}
