package com.teamhide.kream.client.pg

import com.teamhide.kream.common.exception.CustomException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

class PgServerException(
    statusCode: HttpStatusCode,
    message: String,
) : CustomException(
    statusCode = HttpStatus.valueOf(statusCode.value()),
    errorCode = "PG_CLIENT__SERVER_EXCEPTION",
    message = message
)
