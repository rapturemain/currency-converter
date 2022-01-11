package org.rapturemain.currencyconverter

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.WithAssertions
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.rapturemain.currencyconverter.model.Currency
import org.rapturemain.currencyconverter.service.CurrencyConverterApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.SimpleRequestExpectationManager
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

@SpringBootTest
class CurrencyConverterApiServiceTest : WithAssertions {

    @Autowired
    private lateinit var apiService: CurrencyConverterApiService

    @Autowired
    private lateinit var restTemplate: RestTemplate

    private lateinit var mockServer: MockRestServiceServer
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun init(){
        mockServer = MockRestServiceServer.bindTo(restTemplate).build(SimpleRequestExpectationManager())
    }

    @Test
    fun `this test checks cache of available currencies`() {
        mockCurrencies(mockServer, listOf("1", "2", "3"))

        assertThat(apiService.isCurrencyAvailable("1")).isTrue
        assertThat(apiService.isCurrencyAvailable("2")).isTrue
        assertThat(apiService.isCurrencyAvailable("3")).isTrue
        assertThat(apiService.isCurrencyAvailable("4")).isFalse
        assertThat(apiService.isCurrencyAvailable("5")).isFalse

        mockServer.verify()
    }

    @Test
    fun `this test checks currency exchange rate getting and caching`() {
        mockExchangeRate(mockServer, "1_2", 10.0)
        mockExchangeRate(mockServer, "2_1", 0.1)

        assertThat(apiService.getCurrencyExchangeRate(Currency("1"), Currency("2"))).isEqualTo(BigDecimal("10.0"))
        assertThat(apiService.getCurrencyExchangeRate(Currency("1"), Currency("2"))).isEqualTo(BigDecimal("10.0"))
        assertThat(apiService.getCurrencyExchangeRate(Currency("2"), Currency("1"))).isEqualTo(BigDecimal("0.1"))
        assertThat(apiService.getCurrencyExchangeRate(Currency("2"), Currency("1"))).isEqualTo(BigDecimal("0.1"))
        assertThat(apiService.getCurrencyExchangeRate(Currency("1"), Currency("4"))).isNull()

        mockServer.verify()
    }

    private fun mockCurrencies(mockServer: MockRestServiceServer, currencies: List<String>) {
        mockServer.expect(ExpectedCount.once(), requestTo(StringContains("currencies")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(
                    mapOf(
                        "results" to mapOf(
                            *currencies.map { it to mapOf<String, String>() }.toTypedArray()
                        )
                    )
                ))
            )
    }

    private fun mockExchangeRate(mockServer: MockRestServiceServer, currency: String, rate: Double) {
        mockServer.expect(ExpectedCount.once(), requestTo(StringContains(currency)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(
                    mapOf(
                        currency to rate
                    )
                ))
            )
    }
}
