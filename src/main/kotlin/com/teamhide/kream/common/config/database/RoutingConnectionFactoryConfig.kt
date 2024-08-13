package com.teamhide.kream.common.config.database

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.DATABASE
import io.r2dbc.spi.ConnectionFactoryOptions.DRIVER
import io.r2dbc.spi.ConnectionFactoryOptions.HOST
import io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD
import io.r2dbc.spi.ConnectionFactoryOptions.PORT
import io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL
import io.r2dbc.spi.ConnectionFactoryOptions.USER
import io.r2dbc.spi.ConnectionFactoryOptions.builder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class RoutingConnectionFactoryConfig(
    private val connectionFactoryProperties: ConnectionFactoryProperties,
) : AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val readerConnectionFactory = createConnectionFactory(
            property = connectionFactoryProperties.reader,
        )
        val writerConnectionFactory = createConnectionFactory(
            property = connectionFactoryProperties.writer,
        )
        val routingConnectionFactory = RoutingConnectionFactory().apply {
            setTargetConnectionFactories(
                mapOf(
                    ConnectionFactoryType.WRITER to readerConnectionFactory,
                    ConnectionFactoryType.READER to writerConnectionFactory,
                )
            )
            setDefaultTargetConnectionFactory(readerConnectionFactory)
        }
        return routingConnectionFactory
    }

    @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(TransactionAwareConnectionFactoryProxy(connectionFactory))
    }

    private fun createConnectionFactory(property: ConnectionFactoryProperties.ConnectionFactoryProperty): ConnectionFactory {
        return ConnectionFactories.get(
            builder()
                .option(DRIVER, property.driver)
                .option(PROTOCOL, property.protocol)
                .option(HOST, property.host)
                .option(PORT, property.port)
                .option(USER, property.user)
                .option(PASSWORD, property.password)
                .option(DATABASE, property.database)
                .build()
        )
    }
}
