package com.teamhide.kream.client

import com.teamhide.kream.client.pg.PgClientException
import com.teamhide.kream.client.pg.PgServerException
import org.springframework.web.reactive.function.client.WebClientResponseException

suspend fun <T> requestCatching(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: WebClientResponseException) {
        val exception = if (e.statusCode.is4xxClientError) {
            PgClientException(
                statusCode = e.statusCode,
                message = e.responseBodyAsString,
            )
        } else {
            PgServerException(
                statusCode = e.statusCode,
                message = e.responseBodyAsString,
            )
        }
        throw exception
    }
}
