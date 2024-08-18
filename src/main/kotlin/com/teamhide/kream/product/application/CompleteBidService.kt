package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.usecase.CompleteBidCommand
import com.teamhide.kream.product.domain.usecase.CompleteBidUseCase
import org.springframework.stereotype.Service

@Service
class CompleteBidService : CompleteBidUseCase {
    override fun complete(command: CompleteBidCommand) {
        TODO("Not yet implemented")
    }
}
