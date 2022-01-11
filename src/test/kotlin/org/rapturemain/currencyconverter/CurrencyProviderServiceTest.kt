package org.rapturemain.currencyconverter

import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rapturemain.currencyconverter.exception.CurrencyAlreadyExistsException
import org.rapturemain.currencyconverter.exception.CurrencyCodeIsInvalidException
import org.rapturemain.currencyconverter.service.CurrencyProviderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CurrencyProviderServiceTest : WithAssertions {

    @Autowired
    private lateinit var providerService: CurrencyProviderService

    @Test
    fun `this test checks possibility to add already existing currency`() {
        assertThat(providerService.isCurrencyExists("USD")).isTrue

        assertThrows<CurrencyAlreadyExistsException> { providerService.addCurrency("USD") }
    }

    @Test
    fun `this test checks possibility to add wrong currency`() {
        assertThrows<CurrencyCodeIsInvalidException> { providerService.addCurrency("123") }
    }

    @Test
    fun `this test adds and removes custom currency`() {
        val myCurrency = "MYCUSTOMCURRENCY"

        assertThat(providerService.isCurrencyExists(myCurrency)).isFalse

        providerService.addCurrency(myCurrency)

        assertThat(providerService.isCurrencyExists(myCurrency)).isTrue

        providerService.deleteCurrency(myCurrency)

        assertThat(providerService.isCurrencyExists(myCurrency)).isFalse
    }
}