package com.teamhide.kream.product.application

import com.teamhide.kream.user.domain.usecase.UserReaderUseCase
import com.teamhide.kream.user.makeUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class ProductUserAdapterTest : StringSpec({
    val userReaderUseCase = mockk<UserReaderUseCase>()
    val productUserAdapter = ProductUserAdapter(userReaderUseCase = userReaderUseCase)

    "ID로 유저를 조회한다" {
        // Given
        val userId = 1L
        val user = makeUser()
        coEvery { userReaderUseCase.findById(any()) } returns user

        // When
        val sut = productUserAdapter.findById(userId = userId)

        // Then
        sut.shouldNotBeNull()
        sut.id shouldBe user.id
        sut.password shouldBe user.password
        sut.email shouldBe user.email
        sut.nickname shouldBe user.nickname
        sut.address.base shouldBe user.address.base
        sut.address.detail shouldBe user.address.detail
    }
})
