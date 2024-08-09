package com.teamhide.kream.common.config.database

import io.asyncer.r2dbc.mysql.MySqlConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.DATABASE
import io.r2dbc.spi.ConnectionFactoryOptions.DRIVER
import io.r2dbc.spi.ConnectionFactoryOptions.HOST
import io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD
import io.r2dbc.spi.ConnectionFactoryOptions.PORT
import io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL
import io.r2dbc.spi.ConnectionFactoryOptions.USER
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.TransactionAwareConnectionFactoryProxy
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.time.ZoneId

@Configuration
@EnableTransactionManagement
class RoutingConnectionFactoryConfig(
    private val writerConnectionFactoryProperty: WriterConnectionFactoryProperty,
    private val readerConnectionFactoryProperty: ReaderConnectionFactoryProperty,
) : AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val routingConnectionFactory = RoutingConnectionFactory()
        val writerConnectionFactory = writerConnectionFactory()
        val readerConnectionFactory = readerConnectionFactory()
        val connectionFactoryMap = mapOf(
            ConnectionFactoryType.WRITER to writerConnectionFactory,
            ConnectionFactoryType.READER to readerConnectionFactory,
        )

        routingConnectionFactory.setTargetConnectionFactories(connectionFactoryMap)
        routingConnectionFactory.setDefaultTargetConnectionFactory(writerConnectionFactory)

        return routingConnectionFactory
    }

    @Bean
    fun writerConnectionFactory(): ConnectionFactory = makeConnectionFactory(property = writerConnectionFactoryProperty)

    @Bean
    fun readerConnectionFactory(): ConnectionFactory = makeConnectionFactory(property = readerConnectionFactoryProperty)

    @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory): ReactiveTransactionManager {
        return R2dbcTransactionManager(TransactionAwareConnectionFactoryProxy(connectionFactory))
    }

    private fun makeConnectionFactory(property: ConnectionFactoryProperty): ConnectionFactory {
        return ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(DRIVER, property.driver)
                .option(PROTOCOL, property.protocol)
                .option(HOST, property.host)
                .option(PORT, property.port)
                .option(USER, property.user)
                .option(PASSWORD, property.password)
                .option(DATABASE, property.database)
                .option(MySqlConnectionFactoryProvider.SERVER_ZONE_ID, ZoneId.of("Asia/Seoul"))
                .build()
        )
    }
}
