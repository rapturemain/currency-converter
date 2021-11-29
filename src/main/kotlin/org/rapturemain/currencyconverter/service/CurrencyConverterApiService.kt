package org.rapturemain.currencyconverter.service

import lombok.extern.slf4j.Slf4j
import org.rapturemain.currencyconverter.exception.CurrencyConverterApiException
import org.rapturemain.currencyconverter.model.Currency
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.math.BigDecimal

@Component
@Slf4j
class CurrencyConverterApiService
@Autowired constructor(private val restTemplate: RestTemplate) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val exchangeRateCurrencyEncodingTemplate = "%s_%s"
    private val getExchangeRateUrlTemplate = "%s/api/v7/convert?q=%s&compact=ultra&apiKey=%s"
    private val getAvailableCurrencies = "%s/api/v7/currencies?apiKey=%s"

    @Value("\${currencyConverterApi.url}")
    private lateinit var apiUrl: String
    @Value("\${currencyConverterApi.apiKey}")
    private lateinit var apiKey: String

    private var availableCurrencies: Set<String>? = null
    private val lock = Any()

    @Suppress("UNCHECKED_CAST")
    fun isCurrencyAvailable(currencyCode: String): Boolean {
        if (availableCurrencies == null) {
            synchronized(lock) {
                if (availableCurrencies == null) {
                    val url = String.format(getAvailableCurrencies, apiUrl, apiKey)
                    val map = makeRequest(url)
                    val currMap = map["results"] as Map<String, Any>
                    val available = mutableListOf<String>()
                    currMap.entries.forEach { entry ->
                        available.add(entry.key)
                    }
                    availableCurrencies = available.toSet()
                }
            }
        }
        return availableCurrencies!!.contains(currencyCode)
    }

    @Throws(CurrencyConverterApiException::class)
    fun getCurrencyExchangeRate(currencyFrom: Currency, currencyTo: Currency): BigDecimal? {
        if (!isCurrencyAvailable(currencyFrom.currencyCode) || !isCurrencyAvailable(currencyTo.currencyCode)) {
            return null
        }

        val currency = String.format(exchangeRateCurrencyEncodingTemplate, currencyFrom.currencyCode, currencyTo.currencyCode)
        val url = String.format(getExchangeRateUrlTemplate, apiUrl, currency, apiKey)
        val map = makeRequest(url)
        return if (map[currency] == null) null else BigDecimal(map[currency])
    }

    @Throws(CurrencyConverterApiException::class)
    private fun makeRequest(url: String): Map<String, String> {
        log.debug("Sending GET request to ${url.replace(Regex("&apiKey=.*$"), "&apiKey=<API KEY>")}")
        val response = restTemplate.exchange<Map<String, String>>(url, HttpMethod.GET, null, HashMap::class)
        if (response.statusCode != HttpStatus.OK) {
            log.error("CurrConv API failed. Status code: [{}], Response: [{}]", response.statusCode, response.body)
            throw CurrencyConverterApiException("Returned status code is: ${response.statusCode}")
        }
        return response.body!!
    }
}