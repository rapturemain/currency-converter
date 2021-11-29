package org.rapturemain.currencyconverter.service

import org.rapturemain.currencyconverter.exception.NoExchangeRateException
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class CurrencyExchangeRateCacheService {

    private val rates = mutableMapOf<Pair<String, String>, BigDecimal>()

    fun updateRate(currencyFrom: Currency, currencyTo: Currency, rate: BigDecimal) {
        rates[currencyFrom.currencyCode to currencyTo.currencyCode] = rate
    }

    fun getRate(currencyFrom: Currency, currencyTo: Currency) =
            rates[currencyFrom.currencyCode to currencyTo.currencyCode] ?: throw NoExchangeRateException(currencyFrom, currencyTo)
}