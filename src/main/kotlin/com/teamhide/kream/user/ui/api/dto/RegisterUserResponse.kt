package com.teamhide.kream.user.ui.api.dto

import com.teamhide.kream.user.domain.model.User

data class RegisterUserResponse(
    val email: String,
    val nickname: String,
) {
    companion object {
        fun from(user: User): RegisterUserResponse {
            return user.let {
                RegisterUserResponse(email = it.email, nickname = it.nickname)
            }
        }
    }
}
