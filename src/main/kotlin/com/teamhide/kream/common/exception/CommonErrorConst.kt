package com.teamhide.kream.common.exception

import org.springframework.http.HttpStatus

enum class CommonErrorConst(val errorCode: String, val message: String, val statusCode: HttpStatus) {
    HTTP_MESSAGE_NOT_READABLE(
        "HTTP_MESSAGE_NOT_READABLE", "Message not readable", HttpStatus.BAD_REQUEST
    ),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(
        "HTTP_REQUEST_METHOD_NOT_SUPPORTED",
        "Http request method not supported",
        HttpStatus.METHOD_NOT_ALLOWED
    ),
    SERVER_WEB_INPUT_ERROR("SERVER_WEB_INPUT_ERROR", "Server web input error", HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_NOT_VALID(
        "METHOD_ARGUMENT_NOT_VALID", "Method argument not valid", HttpStatus.BAD_REQUEST
    ),
    NO_RESOURCE_FOUND_ERROR("NO_RESOURCE_FOUND_ERROR", "No resource found", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR("AUTHENTICATION_ERROR", "Authentication error", HttpStatus.UNAUTHORIZED),
    NO_HANDLER_FOUND("NO_HANDLER_FOUND", "No endpoint GET URL", HttpStatus.NOT_FOUND),
    UNKNOWN("UNKNOWN", "Unknown", HttpStatus.INTERNAL_SERVER_ERROR),
    MISSING_REQUEST_HEADER(
        "MISSING_REQUEST_HEADER", "Missing request header", HttpStatus.UNPROCESSABLE_ENTITY
    );
}
