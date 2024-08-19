package com.teamhide.kream.client

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider

class WebClientFactory private constructor() {
    companion object {
        private const val MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024

        fun create(clientName: String, baseUrl: String, connectTimoutMilliSeconds: Int, readTimeoutMilleSeconds: Int): WebClient {
            val exchangeStrategies = ExchangeStrategies.builder()
                .codecs { configurer ->
                    configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE)
                }.build()
            val connectionProvider = ConnectionProvider.builder(clientName)
                .maxConnections(100)
                .build()
            val httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimoutMilliSeconds)
                .doOnConnected { connection ->
                    connection.addHandlerLast(ReadTimeoutHandler(readTimeoutMilleSeconds))
                }

            return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders { headers ->
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                }
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .build()
        }
    }
}
