package com.teamhide.kream.support.test

import com.teamhide.kream.common.util.jwt.TokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TestTokenProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
) {
    private val tokenProvider by lazy { TokenProvider(secretKey = secretKey) }

    fun create(userId: Long): String {
        return tokenProvider.encrypt(userId = userId)
    }
}
