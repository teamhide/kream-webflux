package com.teamhide.kream.product.application

import com.teamhide.kream.product.domain.usecase.ProductUserPort
import com.teamhide.kream.user.domain.model.User
import com.teamhide.kream.user.domain.usecase.GetUserByIdQuery
import com.teamhide.kream.user.domain.usecase.UserReaderUseCase
import org.springframework.stereotype.Component

@Component
class ProductUserAdapter(
    private val userReaderUseCase: UserReaderUseCase,
) : ProductUserPort {
    override suspend fun findById(userId: Long): User? {
        val query = GetUserByIdQuery(userId = userId)
        return userReaderUseCase.findById(query = query)
    }
}
