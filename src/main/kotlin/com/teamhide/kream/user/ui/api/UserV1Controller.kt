package com.teamhide.kream.user.ui.api

import com.teamhide.kream.common.response.ApiResponse
import com.teamhide.kream.user.domain.usecase.RegisterUserCommand
import com.teamhide.kream.user.domain.usecase.RegisterUserUseCase
import com.teamhide.kream.user.ui.api.dto.RegisterUserRequest
import com.teamhide.kream.user.ui.api.dto.RegisterUserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/user")
class UserV1Controller(
    private val registerUserUseCase: RegisterUserUseCase,
) {
    @PostMapping("")
    suspend fun registerUser(@RequestBody @Valid body: RegisterUserRequest): ApiResponse<RegisterUserResponse> {
        val command = with(body) {
            RegisterUserCommand(
                email = email,
                nickname = nickname,
                password1 = password1,
                password2 = password2,
                baseAddress = baseAddress,
                detailAddress = detailAddress,
            )
        }
        val user = registerUserUseCase.register(command = command)
        val response = RegisterUserResponse.from(user)
        return ApiResponse.success(body = response, statusCode = HttpStatus.OK)
    }
}
