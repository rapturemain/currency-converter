package org.rapturemain.currencyconverter.service

import org.rapturemain.currencyconverter.exception.NoExchangeRateException
import org.rapturemain.currencyconverter.model.Currency
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap

@Component
class CurrencyExchangeRateCacheService {

    private val rates = ConcurrentHashMap<String, MutableMap<String, BigDecimal>>()

    fun updateRate(currencyFrom: Currency, currencyTo: Currency, rate: BigDecimal) {
        if (!rates.containsKey(currencyFrom.currencyCode)) {
            rates[currencyFrom.currencyCode] = mutableMapOf()
        }
        rates[currencyFrom.currencyCode]!![currencyTo.currencyCode] = rate
    }

    @Throws(NoExchangeRateException::class)
    fun getRate(currencyFrom: Currency, currencyTo: Currency) =
            rates[currencyFrom.currencyCode]?.get(currencyTo.currencyCode) ?: throw NoExchangeRateException(currencyFrom, currencyTo)

    fun deleteRatesOf(currency: Currency) {
        rates.remove(currency.currencyCode)
    }
}