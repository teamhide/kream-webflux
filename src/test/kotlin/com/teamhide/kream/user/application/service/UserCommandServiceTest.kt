package com.teamhide.kream.user.application.service

import com.teamhide.kream.user.application.exception.UserAlreadyExistException
import com.teamhide.kream.user.domain.model.PasswordDoesNotMatchException
import com.teamhide.kream.user.domain.repository.UserRepositoryAdapter
import com.teamhide.kream.user.makeRegisterUserCommand
import com.teamhide.kream.user.makeUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class UserCommandServiceTest : BehaviorSpec({
    val userRepositoryAdapter = mockk<UserRepositoryAdapter>()
    val userCommandService = UserCommandService(userRepositoryAdapter = userRepositoryAdapter)

    Given("password1과 password2가 동일하지 않은 경우") {
        val command = makeRegisterUserCommand(password1 = "a", password2 = "b")

        When("회원가입을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<PasswordDoesNotMatchException> { userCommandService.register(command) }
            }
        }
    }

    Given("동일한 이메일을 가진 유저가 존재하는 경우") {
        val command = makeRegisterUserCommand()
        coEvery { userRepositoryAdapter.existsByEmail(any()) } returns true

        When("회원가입을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<UserAlreadyExistException> { userCommandService.register(command) }
            }
        }
    }

    Given("동일한 이메일 또는 닉네임을 가진 유저가 존재하지 않는 경우") {
        val command = makeRegisterUserCommand()
        coEvery { userRepositoryAdapter.existsByEmail(any()) } returns false

        val user = makeUser()
        coEvery { userRepositoryAdapter.save(any()) } returns user

        When("회원가입을 요청하면") {
            val sut = userCommandService.register(command)

            Then("유저가 저장된다") {
                sut.nickname shouldBe user.nickname
                sut.email shouldBe user.email
                sut.password shouldBe user.password
                sut.baseAddress shouldBe user.baseAddress
                sut.detailAddress shouldBe user.detailAddress
            }
        }
    }
})
