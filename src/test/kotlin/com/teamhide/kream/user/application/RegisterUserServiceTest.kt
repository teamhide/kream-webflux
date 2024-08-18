package com.teamhide.kream.user.application

import com.teamhide.kream.user.application.exception.UserAlreadyExistException
import com.teamhide.kream.user.domain.model.PasswordDoesNotMatchException
import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.makeRegisterUserCommand
import com.teamhide.kream.user.makeUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class RegisterUserServiceTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val registerUserService = RegisterUserService(userRepository = userRepository)

    Given("password1과 password2가 동일하지 않은 경우") {
        val command = makeRegisterUserCommand(password1 = "a", password2 = "b")

        When("회원가입을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<PasswordDoesNotMatchException> { registerUserService.register(command) }
            }
        }
    }

    Given("동일한 이메일을 가진 유저가 존재하는 경우") {
        val command = makeRegisterUserCommand()
        coEvery { userRepository.existsByEmail(any()) } returns true

        When("회원가입을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UserAlreadyExistException> { registerUserService.register(command) }
            }
        }
    }

    Given("동일한 이메일 또는 닉네임을 가진 유저가 존재하지 않는 경우") {
        val command = makeRegisterUserCommand()
        coEvery { userRepository.existsByEmail(any()) } returns false

        val user = makeUser()
        coEvery { userRepository.save(any()) } returns user

        When("회원가입을 요청하면") {
            val sut = registerUserService.register(command)

            Then("유저가 저장된다") {
                sut.nickname shouldBe user.nickname
                sut.email shouldBe user.email
                sut.password shouldBe user.password
                sut.address.base shouldBe user.address.base
                sut.address.detail shouldBe user.address.detail
            }
        }
    }
})
