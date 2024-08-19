package com.teamhide.kream.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.teamhide.kream.product.domain.event.BiddingCompletedEvent
import com.teamhide.kream.support.test.IntegrationTest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList
import java.time.LocalDateTime

@IntegrationTest
class OutboxRepositoryTest(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) : StringSpec({
    afterEach {
        outboxRepository.deleteAll()
    }

    "완료되지 않은 로우를 조회한다" {
        // Given
        val event1 = BiddingCompletedEvent(
            productId = 1L,
            biddingId = 1L,
        )
        val outbox1 = Outbox(
            aggregateType = AggregateType.BIDDING_COMPLETED,
            payload = objectMapper.writeValueAsString(event1),
            completedAt = null,
        )
        val event2 = BiddingCompletedEvent(
            productId = 2L,
            biddingId = 2L,
        )
        val outbox2 = Outbox(
            aggregateType = AggregateType.BIDDING_COMPLETED,
            payload = objectMapper.writeValueAsString(event2),
            completedAt = null,
        )
        val savedOutbox1 = outboxRepository.save(outbox1)
        outboxRepository.save(outbox2)

        // When
        val sut = outboxRepository.findAllBy(limit = 1, offset = 0).toList()

        // Then
        sut.size shouldBe 1
        val outbox = sut[0]
        outbox.id shouldBe savedOutbox1.id
        outbox.completedAt shouldBe null
        outbox.aggregateType shouldBe savedOutbox1.aggregateType
        outbox.payload shouldBe savedOutbox1.payload
    }

    "ID목록으로 완료처리한다" {
        // Given
        val outbox1 = outboxRepository.save(
            Outbox(
                aggregateType = AggregateType.BIDDING_COMPLETED,
                payload = "payload",
                completedAt = null,
            )
        )
        val outbox2 = outboxRepository.save(
            Outbox(
                aggregateType = AggregateType.BIDDING_COMPLETED,
                payload = "payload",
                completedAt = null,
            )
        )

        // When
        val count = outboxRepository.completeByIds(outboxIds = listOf(outbox1.id, outbox2.id), completedAt = LocalDateTime.now())

        // Then
        count shouldBe 2
    }
})
