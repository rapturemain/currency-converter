package org.rapturemain.currencyconverter

import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.rapturemain.currencyconverter.model.Currency
import org.rapturemain.currencyconverter.service.CurrencyExchangeRateCacheService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class CurrencyExchangeRateCacheServiceTest : WithAssertions {

    @Autowired
    private lateinit var cacheService: CurrencyExchangeRateCacheService

    companion object {
        val CURRENCY_1 = Currency("1")
        val CURRENCY_2 = Currency("2")
        val CURRENCY_3 = Currency("3")
    }

    @Test
    fun `this test checks cache entry addition, updating, getting and removing`() {
        val missingRate = cacheService.getRate(CURRENCY_1, CURRENCY_2)
        assertThat(missingRate).isNull()

        val rate1To2 = BigDecimal(10)
        cacheService.updateRate(CURRENCY_1, CURRENCY_2, rate1To2)
        assertThat(cacheService.getRate(CURRENCY_1, CURRENCY_2)).isEqualTo(rate1To2)
        assertThat(cacheService.getRate(CURRENCY_1, CURRENCY_3)).isNull()
        assertThat(cacheService.getRate(CURRENCY_2, CURRENCY_1)).isNull()

        val rate1To2Updated = BigDecimal(20)
        cacheService.updateRate(CURRENCY_1, CURRENCY_2, rate1To2Updated)
        assertThat(cacheService.getRate(CURRENCY_1, CURRENCY_2)).isEqualTo(rate1To2Updated)
        assertThat(cacheService.getRate(CURRENCY_1, CURRENCY_3)).isNull()
        assertThat(cacheService.getRate(CURRENCY_2, CURRENCY_1)).isNull()

        cacheService.deleteRatesOf(CURRENCY_1)
        assertThat(cacheService.getRate(CURRENCY_1, CURRENCY_2)).isNull()
        assertThat(cacheService.getRate(CURRENCY_1, CURRENCY_3)).isNull()
        assertThat(cacheService.getRate(CURRENCY_2, CURRENCY_1)).isNull()
    }
}