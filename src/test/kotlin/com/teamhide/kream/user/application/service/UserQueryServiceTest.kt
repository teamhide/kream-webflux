package com.teamhide.kream.user.application.service

import com.teamhide.kream.user.domain.repository.UserRepositoryAdapter
import com.teamhide.kream.user.domain.usecase.GetUserByIdQuery
import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class UserQueryServiceTest : BehaviorSpec({
    val userRepositoryAdapter = mockk<UserRepositoryAdapter>()
    val userQueryService = UserQueryService(userRepositoryAdapter = userRepositoryAdapter)

    Given("유저 ID를 기반으로") {
        val query = GetUserByIdQuery(userId = 1L)
        val user = makeUser()
        coEvery { userRepositoryAdapter.findById(any()) } returns user

        When("조회하면") {
            val sut = userQueryService.findById(query = query)

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
})
