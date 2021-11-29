package org.rapturemain.currencyconverter.service

import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class CurrencyProviderService {

    private val availableCurrenciesMap = mutableMapOf<String, Currency>()
    private val availableCurrencies = mutableListOf<String>()

    @PostConstruct
    fun start() {
        val currencies = Currency.getAvailableCurrencies()

        currencies.forEach {
            availableCurrenciesMap[it.currencyCode] = it
        }

        availableCurrencies.addAll(currencies.map { it.currencyCode })
    }

    fun isCurrencyExists(currencyCode: String) = availableCurrenciesMap.containsKey(currencyCode)

    fun getCurrencies() = availableCurrencies

    @Throws(NoCurrencyExistsException::class)
    fun getCurrencyByCode(currencyCode: String) =
            availableCurrenciesMap[currencyCode] ?: throw NoCurrencyExistsException(currencyCode)
}