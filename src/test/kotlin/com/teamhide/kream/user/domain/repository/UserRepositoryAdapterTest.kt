package com.teamhide.kream.user.domain.repository

import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class UserRepositoryAdapterTest : BehaviorSpec({
    val userRepository = mockk<UserRepository>()
    val userRepositoryAdapter = UserRepositoryAdapter(userRepository = userRepository)

    Given("Email에 해당하는 유저가 존재할 때") {
        coEvery { userRepository.existsByEmail(any()) } returns true
        val email = "h@id.e"

        When("존재 여부를 확인하면") {
            val sut = userRepositoryAdapter.existsByEmail(email = email)

            Then("유저 존재 여부를 반환한다") {
                sut shouldBe true
            }
        }
    }

    Given("새로운 유저를") {
        val user = makeUser()
        coEvery { userRepository.save(any()) } returns user

        When("저장하는 경우") {
            val sut = userRepositoryAdapter.save(user)

            Then("저장된 유저를 리턴한다") {
                sut.id shouldBe user.id
                sut.password shouldBe user.password
                sut.email shouldBe user.email
                sut.nickname shouldBe user.nickname
                sut.baseAddress shouldBe user.baseAddress
                sut.detailAddress shouldBe user.detailAddress
                coVerify(exactly = 1) { userRepository.save(any()) }
            }
        }
    }

    Given("ID에 해당하는 유저가 있는 경우") {
        val user = makeUser()
        coEvery { userRepository.findById(any()) } returns user

        When("유저를 ID로 조회하면") {
            val sut = userRepositoryAdapter.findById(user.id)

            Then("유저 정보를 리턴한다") {
                sut.shouldNotBeNull()
                sut.id shouldBe user.id
                sut.password shouldBe user.password
                sut.email shouldBe user.email
                sut.baseAddress shouldBe user.baseAddress
                sut.detailAddress shouldBe user.detailAddress
            }
        }
    }
})
