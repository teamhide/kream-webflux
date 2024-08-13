package com.teamhide.kream.common.security

data class AuthenticationErrorResponse(
    val errorCode: String = "UNAUTHORIZED",
    val message: String = "Authentication error",
)
