package com.teamhide.kream.outbox

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "outbox")
class Outbox(
    @Column("aggregate_type")
    val aggregateType: AggregateType,

    @Column("payload")
    val payload: String,

    @Column("completed_at")
    @Value("null")
    var completedAt: LocalDateTime? = null,

    @Id
    val id: Long = 0L,
) {
    fun complete() {
        this.completedAt = LocalDateTime.now()
    }
}
