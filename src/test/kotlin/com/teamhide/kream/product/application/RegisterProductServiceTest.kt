package com.teamhide.kream.product.application

import com.teamhide.kream.product.application.exception.ProductBrandNotFoundException
import com.teamhide.kream.product.application.exception.ProductCategoryNotFoundException
import com.teamhide.kream.product.domain.model.InvalidReleasePriceException
import com.teamhide.kream.product.domain.repository.ProductDisplayRepository
import com.teamhide.kream.product.domain.repository.ProductRepository
import com.teamhide.kream.product.domain.usecase.ProductReaderUseCase
import com.teamhide.kream.product.makeProduct
import com.teamhide.kream.product.makeProductBrand
import com.teamhide.kream.product.makeProductCategory
import com.teamhide.kream.product.makeProductDisplay
import com.teamhide.kream.product.makeRegisterProductCommand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class RegisterProductServiceTest : BehaviorSpec({
    val productReaderUseCase = mockk<ProductReaderUseCase>()
    val productRepository = mockk<ProductRepository>()
    val productDisplayRepository = mockk<ProductDisplayRepository>()
    val registerProductService = RegisterProductService(
        productReaderUseCase = productReaderUseCase,
        productRepository = productRepository,
        productDisplayRepository = productDisplayRepository,
    )

    Given("존재하지 않는 브랜드의") {
        val command = makeRegisterProductCommand()

        coEvery { productReaderUseCase.findBrandById(any()) } returns null

        When("상품 등록을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductBrandNotFoundException> {
                    registerProductService.registerProduct(command = command)
                }
            }
        }
    }

    Given("존재하지 않는 카테고리의") {
        val command = makeRegisterProductCommand()
        val productBrand = makeProductBrand()

        coEvery { productReaderUseCase.findBrandById(any()) } returns productBrand
        coEvery { productReaderUseCase.findCategoryById(any()) } returns null

        When("상품 등록을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductCategoryNotFoundException> {
                    registerProductService.registerProduct(command = command)
                }
            }
        }
    }

    Given("상품 가격이 0원 이하일 때") {
        val command = makeRegisterProductCommand(releasePrice = -1)
        val productBrand = makeProductBrand()
        val productCategory = makeProductCategory()

        coEvery { productReaderUseCase.findBrandById(any()) } returns productBrand
        coEvery { productReaderUseCase.findCategoryById(any()) } returns productCategory

        When("상품 등록을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidReleasePriceException> {
                    registerProductService.registerProduct(command = command)
                }
            }
        }
    }

    Given("존재하는 브랜드/카테고리를 대상으로") {
        val command = makeRegisterProductCommand(releasePrice = 10000)
        val productBrand = makeProductBrand()
        val productCategory = makeProductCategory()

        coEvery { productReaderUseCase.findBrandById(any()) } returns productBrand

        coEvery { productReaderUseCase.findCategoryById(any()) } returns productCategory

        val product = makeProduct()
        coEvery { productRepository.save(any()) } returns product

        val productDisplay = makeProductDisplay()
        coEvery { productDisplayRepository.save(any()) } returns productDisplay

        When("상품 등록을 요청하면") {
            val sut = registerProductService.registerProduct(command = command)

            Then("등록된 상품 정보가 리턴된다") {
                sut.id shouldBe product.id
                sut.name shouldBe product.name
                sut.sizeType shouldBe product.sizeType
                sut.modelNumber shouldBe product.modelNumber
                sut.releasePrice shouldBe product.releasePrice
                sut.brand shouldBe productBrand.name
                sut.category shouldBe productCategory.name
            }

            Then("상품 정보를 저장한다") {
                coVerify(exactly = 1) { productRepository.save(any()) }
                coVerify(exactly = 1) { productDisplayRepository.save(any()) }
            }
        }
    }
})
