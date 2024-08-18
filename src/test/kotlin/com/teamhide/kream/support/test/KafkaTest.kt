package com.teamhide.kream.support.test

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.annotation.AliasFor
import org.springframework.kafka.test.context.EmbeddedKafka

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestEnvironment
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092"],
    ports = [9092],
)
annotation class KafkaTest(
    @get:AliasFor(annotation = EmbeddedKafka::class, attribute = "topics")
    val topics: Array<String> = []
)
