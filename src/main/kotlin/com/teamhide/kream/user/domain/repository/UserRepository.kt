package com.teamhide.kream.user.domain.repository

import com.teamhide.kream.user.domain.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long> {
    suspend fun existsByEmail(email: String): Boolean
}
