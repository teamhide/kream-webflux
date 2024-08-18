package com.teamhide.kream.user.application

import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.domain.usecase.GetUserByIdQuery
import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class UserReaderTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val userReader = UserReader(userRepository = userRepository)

    Given("유저 ID를 기반으로") {
        val query = GetUserByIdQuery(userId = 1L)
        val user = makeUser()
        coEvery { userRepository.findById(any()) } returns user

        When("조회하면") {
            val sut = userReader.findById(query = query)

            Then("유저 정보가 반환된다") {
                sut.shouldNotBeNull()
                sut.id shouldBe user.id
                sut.password shouldBe user.password
                sut.email shouldBe user.email
                sut.nickname shouldBe user.nickname
                sut.address.base shouldBe user.address.base
                sut.address.detail shouldBe user.address.detail
            }
        }
    }

    Given("Email에 해당하는 유저가 존재할 때") {
        coEvery { userRepository.existsByEmail(any()) } returns true
        val email = "h@id.e"

        When("존재 여부를 확인하면") {
            val sut = userReader.existsByEmail(email = email)

            Then("유저 존재 여부를 반환한다") {
                sut shouldBe true
            }
        }
    }
})
