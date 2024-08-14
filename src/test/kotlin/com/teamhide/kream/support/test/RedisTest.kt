package com.teamhide.kream.support.test

import com.teamhide.kream.common.config.RedisConfig
import org.springframework.boot.test.context.SpringBootTest

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [RedisConfig::class])
@TestEnvironment
annotation class RedisTest
