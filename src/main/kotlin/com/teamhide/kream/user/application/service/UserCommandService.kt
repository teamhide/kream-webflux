package com.teamhide.kream.user.application.service

import com.teamhide.kream.user.application.exception.UserAlreadyExistException
import com.teamhide.kream.user.domain.model.User
import com.teamhide.kream.user.domain.repository.UserRepositoryAdapter
import com.teamhide.kream.user.domain.usecase.RegisterUserCommand
import com.teamhide.kream.user.domain.usecase.RegisterUserUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepositoryAdapter: UserRepositoryAdapter,
) : RegisterUserUseCase {
    override suspend fun register(command: RegisterUserCommand): User {
        val user = User.create(
            email = command.email,
            nickname = command.nickname,
            password1 = command.password1,
            password2 = command.password2,
            baseAddress = command.baseAddress,
            detailAddress = command.detailAddress,
        )
        if (userRepositoryAdapter.existsByEmail(email = command.email)) {
            throw UserAlreadyExistException()
        }
        return userRepositoryAdapter.save(user = user)
    }
}
