package com.teamhide.kream.user.application

import com.teamhide.kream.user.domain.model.User
import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.domain.usecase.GetUserByIdQuery
import com.teamhide.kream.user.domain.usecase.UserReaderUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserReader(
    private val userRepository: UserRepository,
) : UserReaderUseCase {
    override suspend fun findById(query: GetUserByIdQuery): User? {
        return userRepository.findById(query.userId)
    }

    override suspend fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email = email)
    }
}
