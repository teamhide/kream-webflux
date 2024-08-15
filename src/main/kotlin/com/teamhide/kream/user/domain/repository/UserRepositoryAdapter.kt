package com.teamhide.kream.user.domain.repository

import com.teamhide.kream.user.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserRepositoryAdapter(
    private val userRepository: UserRepository,
) {
    suspend fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email = email)
    }

    suspend fun save(user: User): User {
        return userRepository.save(user)
    }

    suspend fun findById(userId: Long): User? {
        return userRepository.findById(userId)
    }
}
