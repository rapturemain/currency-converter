package org.rapturemain.currencyconverter.service

import org.rapturemain.currencyconverter.exception.CurrencyAlreadyExistsException
import org.rapturemain.currencyconverter.exception.CurrencyCodeIsInvalidException
import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.rapturemain.currencyconverter.model.Currency
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class CurrencyProviderService {

    private val availableCurrenciesMap = mutableMapOf<String, Currency>()
    private val availableCurrencies = mutableListOf<String>()

    @PostConstruct
    fun start() {
        val currencies = java.util.Currency.getAvailableCurrencies()

        currencies.forEach {
            availableCurrenciesMap[it.currencyCode] = Currency(it.currencyCode, it)
        }

        availableCurrencies.addAll(currencies.map { it.currencyCode })
    }

    fun isCurrencyExists(currencyCode: String) = availableCurrenciesMap.containsKey(currencyCode)

    fun getCurrencies() = availableCurrencies.toList()

    @Throws(NoCurrencyExistsException::class)
    fun getCurrencyByCode(currencyCode: String) =
            availableCurrenciesMap[currencyCode] ?: throw NoCurrencyExistsException(currencyCode)

    @Throws(CurrencyAlreadyExistsException::class, CurrencyCodeIsInvalidException::class)
    fun addCurrency(currencyCode: String) {
        if (!currencyCode.matches(Regex("^[A-Z]+$"))) {
            throw CurrencyCodeIsInvalidException(currencyCode)
        }
        if (isCurrencyExists(currencyCode)) {
            throw CurrencyAlreadyExistsException(currencyCode)
        }

        availableCurrencies.add(currencyCode)
        availableCurrenciesMap[currencyCode] = Currency(currencyCode)
    }
}