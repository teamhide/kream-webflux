package com.teamhide.kream.user.ui.api

import com.teamhide.kream.support.test.IntegrationTest
import com.teamhide.kream.user.USER_ID_1_TOKEN
import com.teamhide.kream.user.application.exception.UserAlreadyExistException
import com.teamhide.kream.user.domain.repository.UserRepository
import com.teamhide.kream.user.makeUser
import com.teamhide.kream.user.ui.api.dto.RegisterUserRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

private const val URL = "/v1/user"

@IntegrationTest
class UserV1ControllerTest(
    private val userRepository: UserRepository,
    private val webTestClient: WebTestClient,
) : BehaviorSpec({
    afterEach {
        userRepository.deleteAll()
    }

    Given("동일한 이메일의 유저가 존재할 때") {
        println("GIVEN")
        val user = makeUser(
            email = "h@id.e",
            nickname = "test",
            password = "a",
            baseAddress = "base",
            detailAddress = "detail",
        )
        userRepository.save(user)

        val request = RegisterUserRequest(
            email = user.email,
            nickname = "test",
            password1 = "a",
            password2 = "a",
            baseAddress = "base",
            detailAddress = "detail",
        )
        val exc = UserAlreadyExistException()

        When("회원가입을 요청하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("400 응답이 내려온다") {
                response.expectStatus().isBadRequest
                    .expectBody()
                    .jsonPath("errorCode").isEqualTo(exc.errorCode)
                    .jsonPath("message").isEqualTo(exc.message)
                userRepository.count() shouldBe 1
            }
        }
    }

    Given("동일한 이메일의 유저가 없을 때") {
        val request = RegisterUserRequest(
            email = "h@id.e",
            nickname = "test",
            password1 = "a",
            password2 = "a",
            baseAddress = "base",
            detailAddress = "detail",
        )

        When("회원가입을 요청하면") {
            val response = webTestClient.post()
                .uri(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $USER_ID_1_TOKEN")
                .body(BodyInserters.fromValue(request))
                .exchange()

            Then("200 응답이 내려온다") {
                response.expectStatus().isOk
                    .expectBody()
                    .jsonPath("email").isEqualTo(request.email)
                    .jsonPath("nickname").isEqualTo(request.nickname)
                userRepository.count() shouldBe 1
            }
        }
    }
})
