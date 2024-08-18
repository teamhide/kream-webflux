package com.teamhide.kream.user.domain.usecase

import com.teamhide.kream.user.domain.model.User

data class GetUserByIdQuery(val userId: Long)

interface UserReaderUseCase {
    suspend fun findById(query: GetUserByIdQuery): User?
    suspend fun existsByEmail(email: String): Boolean
}
