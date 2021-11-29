package org.rapturemain.currencyconverter.service

import org.rapturemain.currencyconverter.exception.CurrencyConverterApiException
import org.rapturemain.currencyconverter.exception.InvalidRateException
import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.rapturemain.currencyconverter.exception.NoExchangeRateException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CurrencyExchangeRateProviderService
@Autowired constructor(private val currencyProvider: CurrencyProviderService,
                       private val rateCache: CurrencyExchangeRateCacheService,
                       private val currConvApi: CurrencyConverterApiService) {

    @Throws(NoExchangeRateException::class, InvalidRateException::class)
    fun updateCache(currencyFrom: String, currencyTo: String, rate: String) {
        val rateBD = try {
            BigDecimal(rate)
        } catch (ex: Exception) {
            throw InvalidRateException(rate)
        }

        rateCache.updateRate(currencyProvider.getCurrencyByCode(currencyFrom), currencyProvider.getCurrencyByCode(currencyTo), rateBD)
    }

    @Throws(NoExchangeRateException::class, NoCurrencyExistsException::class, CurrencyConverterApiException::class)
    fun getRate(currencyFrom: String, currencyTo: String): BigDecimal {
        val currFrom = currencyProvider.getCurrencyByCode(currencyFrom)
        val currTo = currencyProvider.getCurrencyByCode(currencyTo)
        val overriddenRate = rateCache.getRate(currFrom, currTo)
        if (overriddenRate != null) {
            return overriddenRate
        }

        val rate = currConvApi.getCurrencyExchangeRate(currFrom, currTo)
        if (rate != null) {
            return rate
        }

        throw NoExchangeRateException("Cannot get exchange rate of $currencyFrom to $currencyTo")
    }

    @Throws(NoCurrencyExistsException::class)
    fun deleteRatesOf(currency: String) {
        rateCache.deleteRatesOf(currencyProvider.getCurrencyByCode(currency))
    }

}