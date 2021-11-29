package org.rapturemain.currencyconverter.service

import org.rapturemain.currencyconverter.exception.InvalidRateException
import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.rapturemain.currencyconverter.exception.NoExchangeRateException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CurrencyExchangeRateProviderService
@Autowired constructor(private val currencyProvider: CurrencyProviderService,
                       private val rateCache: CurrencyExchangeRateCacheService) {

    @Throws(NoExchangeRateException::class, InvalidRateException::class)
    fun updateCache(currencyFrom: String, currencyTo: String, rate: String) {
        val rateBD = try {
            BigDecimal(rate)
        } catch (ex: Exception) {
            throw InvalidRateException(rate)
        }

        rateCache.updateRate(currencyProvider.getCurrencyByCode(currencyFrom), currencyProvider.getCurrencyByCode(currencyTo), rateBD)
    }

    @Throws(NoExchangeRateException::class, NoCurrencyExistsException::class)
    fun getRate(currencyFrom: String, currencyTo: String): BigDecimal =
            rateCache.getRate(currencyProvider.getCurrencyByCode(currencyFrom), currencyProvider.getCurrencyByCode(currencyTo))

    @Throws(NoCurrencyExistsException::class)
    fun deleteRatesOf(currency: String) {
        rateCache.deleteRatesOf(currencyProvider.getCurrencyByCode(currency))
    }

}