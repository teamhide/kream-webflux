package com.teamhide.kream.product.application.service

import com.teamhide.kream.product.application.exception.ProductBrandNotFoundException
import com.teamhide.kream.product.application.exception.ProductCategoryNotFoundException
import com.teamhide.kream.product.domain.model.InvalidReleasePriceException
import com.teamhide.kream.product.domain.repository.ProductDisplayRepositoryAdapter
import com.teamhide.kream.product.domain.repository.ProductRepositoryAdapter
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

class ProductCommandServiceTest : BehaviorSpec({
    val productRepositoryAdapter = mockk<ProductRepositoryAdapter>()
    val productDisplayRepositoryAdapter = mockk<ProductDisplayRepositoryAdapter>()
    val productCommandService = ProductCommandService(
        productRepositoryAdapter = productRepositoryAdapter,
        productDisplayRepositoryAdapter = productDisplayRepositoryAdapter,
    )

    Given("존재하지 않는 브랜드의") {
        val command = makeRegisterProductCommand()

        coEvery { productRepositoryAdapter.findBrandById(any()) } returns null

        When("상품 등록을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductBrandNotFoundException> {
                    productCommandService.registerProduct(command = command)
                }
            }
        }
    }

    Given("존재하지 않는 카테고리의") {
        val command = makeRegisterProductCommand()
        val productBrand = makeProductBrand()

        coEvery { productRepositoryAdapter.findBrandById(any()) } returns productBrand
        coEvery { productRepositoryAdapter.findCategoryById(any()) } returns null

        When("상품 등록을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<ProductCategoryNotFoundException> {
                    productCommandService.registerProduct(command = command)
                }
            }
        }
    }

    Given("상품 가격이 0원 이하일 때") {
        val command = makeRegisterProductCommand(releasePrice = -1)
        val productBrand = makeProductBrand()
        val productCategory = makeProductCategory()

        coEvery { productRepositoryAdapter.findBrandById(any()) } returns productBrand
        coEvery { productRepositoryAdapter.findCategoryById(any()) } returns productCategory

        When("상품 등록을 요청하면") {
            Then("예외가 발생한다") {
                shouldThrow<InvalidReleasePriceException> {
                    productCommandService.registerProduct(command = command)
                }
            }
        }
    }

    Given("존재하는 브랜드/카테고리를 대상으로") {
        val command = makeRegisterProductCommand(releasePrice = 10000)
        val productBrand = makeProductBrand()
        val productCategory = makeProductCategory()

        coEvery { productRepositoryAdapter.findBrandById(any()) } returns productBrand

        coEvery { productRepositoryAdapter.findCategoryById(any()) } returns productCategory

        val product = makeProduct()
        coEvery { productRepositoryAdapter.saveProduct(any()) } returns product

        val productDisplay = makeProductDisplay()
        coEvery { productDisplayRepositoryAdapter.save(any()) } returns productDisplay

        When("상품 등록을 요청하면") {
            val sut = productCommandService.registerProduct(command = command)

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
                coVerify(exactly = 1) { productRepositoryAdapter.saveProduct(any()) }
                coVerify(exactly = 1) { productDisplayRepositoryAdapter.save(any()) }
            }
        }
    }
})
