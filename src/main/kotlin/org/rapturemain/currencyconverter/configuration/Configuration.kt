package org.rapturemain.currencyconverter.configuration

import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory

import org.springframework.web.client.RestTemplate




@Configuration
class Configuration {

    @Bean
    fun restTemplate(): RestTemplate {
        val template = RestTemplate()
        val connectionManager = PoolingHttpClientConnectionManager().apply {
            maxTotal = 100
            defaultMaxPerRoute = 6
        }
        template.requestFactory = HttpComponentsClientHttpRequestFactory(HttpClients.custom().setConnectionManager(connectionManager).build())
        return template
    }
}