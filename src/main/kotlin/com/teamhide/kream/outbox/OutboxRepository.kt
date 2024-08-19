package com.teamhide.kream.outbox

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime

interface OutboxRepository : CoroutineCrudRepository<Outbox, Long> {
    @Query("SELECT o.* FROM outbox o WHERE o.completed_at IS NULL ORDER BY o.id LIMIT :limit OFFSET :offset FOR UPDATE SKIP LOCKED")
    suspend fun findAllBy(limit: Int, offset: Int): Flow<Outbox>

    @Modifying
    @Query("UPDATE outbox SET completed_at = :completedAt WHERE id IN (:outboxIds)")
    suspend fun completeByIds(outboxIds: List<Long>, completedAt: LocalDateTime): Long
}
