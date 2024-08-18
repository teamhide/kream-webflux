package com.teamhide.kream.product.ui.api

import com.teamhide.kream.common.response.ApiResponse
import com.teamhide.kream.common.security.CurrentUser
import com.teamhide.kream.product.application.service.BiddingCommandService
import com.teamhide.kream.product.domain.usecase.BidCommand
import com.teamhide.kream.product.domain.usecase.ImmediatePurchaseCommand
import com.teamhide.kream.product.ui.api.dto.BidRequest
import com.teamhide.kream.product.ui.api.dto.BidResponse
import com.teamhide.kream.product.ui.api.dto.ImmediatePurchaseRequest
import com.teamhide.kream.product.ui.api.dto.ImmediatePurchaseResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/bid")
class BidProductV1Controller(
    private val biddingCommandService: BiddingCommandService,
) {
    @PostMapping("")
    suspend fun bid(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestBody @Valid body: BidRequest,
    ): ApiResponse<BidResponse> {
        val command = body.let {
            BidCommand(
                productId = it.productId,
                price = it.price,
                size = it.size,
                biddingType = it.biddingType,
                userId = currentUser.id,
            )
        }
        val bidResponseDto = biddingCommandService.bid(command = command)
        val response = BidResponse.from(bidResponseDto)
        return ApiResponse.success(body = response, statusCode = HttpStatus.OK)
    }

    @PostMapping("/purchase")
    suspend fun immediatePurchase(
        @AuthenticationPrincipal currentUser: CurrentUser,
        @RequestBody @Valid body: ImmediatePurchaseRequest
    ): ApiResponse<ImmediatePurchaseResponse> {
        val command = ImmediatePurchaseCommand(biddingId = body.biddingId, userId = currentUser.id)
        val responseDto = biddingCommandService.immediatePurchase(command = command)
        val response = ImmediatePurchaseResponse.from(responseDto)
        return ApiResponse.success(body = response, statusCode = HttpStatus.OK)
    }
}
