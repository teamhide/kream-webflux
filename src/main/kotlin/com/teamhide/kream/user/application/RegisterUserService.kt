package com.teamhide.kream.user.application

import com.teamhide.kream.user.application.exception.UserAlreadyExistException
import com.teamhide.kream.user.domain.model.User
import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.domain.usecase.RegisterUserCommand
import com.teamhide.kream.user.domain.usecase.RegisterUserUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterUserService(
    private val userRepository: UserRepository,
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
        if (userRepository.existsByEmail(email = command.email)) {
            throw UserAlreadyExistException()
        }
        return userRepository.save(user)
    }
}
