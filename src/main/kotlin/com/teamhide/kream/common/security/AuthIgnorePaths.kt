package com.teamhide.kream.common.security

import org.springframework.http.HttpMethod
import org.springframework.util.AntPathMatcher

enum class AuthType {
    JWT,
}

data class AuthIgnorePath(val authType: AuthType, val method: HttpMethod, val path: String)

class AuthIgnorePaths private constructor() {

    companion object {
        private val matcher: AntPathMatcher = AntPathMatcher()
        private var authIgnoreMaps = mapOf<AuthType, List<AuthIgnorePath>>()
        val authIgnorePaths = mutableSetOf<String>()

        init {
            authIgnoreMaps = makeMaps()
        }

        private fun makeMaps(): Map<AuthType, List<AuthIgnorePath>> {
            val conditions = mutableListOf<AuthIgnorePath>()

            // JWT auth ignore paths
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.GET, path = "/actuator/health"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.GET, path = "/test-auth-not-required"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.GET, path = "/docs/**"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.GET, path = "/actuator/health/readiness"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.GET, path = "/actuator/health/liveness"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.POST, path = "/v1/user"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.GET, path = "/v1/product"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.POST, path = "/pg/payment"))
            conditions.add(AuthIgnorePath(authType = AuthType.JWT, method = HttpMethod.POST, path = "/pg/cancel"))

            addToIgnorePaths(conditions = conditions)
            return conditions.groupBy { it.authType }
        }

        private fun addToIgnorePaths(conditions: List<AuthIgnorePath>) {
            conditions.forEach { authIgnorePaths.add(it.path) }
        }

        fun contain(authType: AuthType, method: HttpMethod, path: String): Boolean {
            val paths = authIgnoreMaps[authType]
            return paths.orEmpty().any {
                matcher.match(it.path, path) && it.method == method
            }
        }
    }
}
