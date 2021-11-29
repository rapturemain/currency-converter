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
import kotlin.jvm.Throws

@Component
@Slf4j
class CurrencyConverterApiService
@Autowired constructor(private val restTemplate: RestTemplate) {
    val log = LoggerFactory.getLogger(this.javaClass)

    val exchangeRateCurrencyEncodingTemplate = "%s_%s"
    val getExchangeRateUrlTemplate = "%s/api/v7/convert?q=%s&compact=ultra&apiKey=%s"

    @Value("\${currencyConverterApi.url}")
    lateinit var apiUrl: String
    @Value("\${currencyConverterApi.apiKey}")
    lateinit var apiKey: String

    @Throws(CurrencyConverterApiException::class)
    fun getCurrencyExchangeRate(currencyFrom: Currency, currencyTo: Currency): BigDecimal? {
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