package com.teamhide.kream.product.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.teamhide.kream.product.domain.event.BiddingCompletedEvent
import com.teamhide.kream.product.domain.event.BiddingCreatedEvent
import com.teamhide.kream.product.domain.model.BiddingType
import com.teamhide.kream.support.test.KafkaTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils

@KafkaTest(
    topics = [
        "\${spring.kafka.topic.bidding-created}",
        "\${spring.kafka.topic.bidding-completed}",
    ]
)
class BiddingKafkaAdapterTest(
    @Value("\${spring.kafka.topic.bidding-created}")
    private val biddingCreatedTopic: String,

    @Value("\${spring.kafka.topic.bidding-completed}")
    private val biddingCompletedTopic: String,

    private val embeddedKafkaBroker: EmbeddedKafkaBroker,
    private val objectMapper: ObjectMapper,
    private val biddingKafkaAdapter: BiddingKafkaAdapter,
) : StringSpec({
    lateinit var consumer: Consumer<String, String>

    beforeEach {
        embeddedKafkaBroker.afterPropertiesSet()
        val configs = KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker)
        consumer = DefaultKafkaConsumerFactory(configs, StringDeserializer(), StringDeserializer()).createConsumer()
    }

    afterEach {
        consumer.close()
    }

    "입찰 생성 메시지를 발송한다" {
        // Given
        val message = BiddingCreatedEvent(
            productId = 1L, price = 2000, biddingType = BiddingType.SALE.name, biddingId = 1L
        )

        // When
        biddingKafkaAdapter.sendBiddingCreated(event = message)

        // Then
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, biddingCreatedTopic)
        val record = KafkaTestUtils.getSingleRecord(consumer, biddingCreatedTopic)
        val receivedMessage = objectMapper.readValue(record.value(), BiddingCreatedEvent::class.java)
        receivedMessage.productId shouldBe message.productId
        receivedMessage.biddingType shouldBe message.biddingType
        receivedMessage.price shouldBe message.price
    }

    "입찰 종료 메시지를 발송한다" {
        // Given
        val message = BiddingCompletedEvent(biddingId = 1L, productId = 1L)

        // When
        biddingKafkaAdapter.sendBiddingCompleted(event = message)

        // Then
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, biddingCompletedTopic)
        val record = KafkaTestUtils.getSingleRecord(consumer, biddingCompletedTopic)
        val receivedMessage = objectMapper.readValue(record.value(), BiddingCompletedEvent::class.java)
        receivedMessage.productId shouldBe message.productId
    }
})
