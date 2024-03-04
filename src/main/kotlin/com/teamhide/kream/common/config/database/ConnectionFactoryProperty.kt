package com.teamhide.kream.common.config.database

import org.springframework.boot.context.properties.ConfigurationProperties

interface ConnectionFactoryProperty {
    val driver: String
    val protocol: String
    val host: String
    val port: Int
    val user: String
    val password: String
    val database: String
}

@ConfigurationProperties(prefix = "spring.r2dbc.writer")
data class WriterConnectionFactoryProperty(
    override val driver: String,
    override val protocol: String,
    override val host: String,
    override val port: Int,
    override val user: String,
    override val password: String,
    override val database: String,
) : ConnectionFactoryProperty

@ConfigurationProperties(prefix = "spring.r2dbc.reader")
data class ReaderConnectionFactoryProperty(
    override val driver: String,
    override val protocol: String,
    override val host: String,
    override val port: Int,
    override val user: String,
    override val password: String,
    override val database: String,
) : ConnectionFactoryProperty
