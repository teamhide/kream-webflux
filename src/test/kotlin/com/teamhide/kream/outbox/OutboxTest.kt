package com.teamhide.kream.outbox

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe

class OutboxTest : StringSpec({
    "completedAt에 현재 날짜를 입력한다" {
        // Given
        val outbox = Outbox(
            aggregateType = AggregateType.BIDDING_COMPLETED,
            payload = "payload",
            completedAt = null,
        )

        // When
        outbox.complete()

        // Then
        outbox.completedAt shouldNotBe null
    }
})
