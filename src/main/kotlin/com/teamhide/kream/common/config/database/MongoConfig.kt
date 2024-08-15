package com.teamhide.kream.common.config.database

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing

@Configuration
@EnableMongoAuditing
class MongoConfig(
    @Value("\${spring.data.mongodb.database}") val database: String,
) : AbstractReactiveMongoConfiguration() {
    override fun getDatabaseName(): String {
        return database
    }

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create()
    }
}
