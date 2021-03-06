package org.rapturemain.currencyconverter.controller

import org.rapturemain.currencyconverter.exception.CurrencyAlreadyExistsException
import org.rapturemain.currencyconverter.exception.CurrencyCodeIsInvalidException
import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.rapturemain.currencyconverter.exceptionhandler.ParameterInvalidException
import org.rapturemain.currencyconverter.exceptionhandler.UnknownException
import org.rapturemain.currencyconverter.model.RecordsWrapper
import org.rapturemain.currencyconverter.service.CurrencyExchangeRateProviderService
import org.rapturemain.currencyconverter.service.CurrencyProviderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/restapi/v1/currencies/")
class CurrenciesController
@Autowired constructor(val currencyProvider: CurrencyProviderService,
                       val exchangeRateProvider: CurrencyExchangeRateProviderService){

    @PutMapping
    fun addCurrency(@RequestParam code: String): ResponseEntity<Any> {
        try {
            currencyProvider.addCurrency(code)
            return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
        } catch (ex: Exception) {
            when (ex) {
                is CurrencyAlreadyExistsException -> throw ParameterInvalidException("code", ex.message)
                is CurrencyCodeIsInvalidException -> throw ParameterInvalidException("code", ex.message)
                else -> throw UnknownException(ex)
            }
        }
    }

    @GetMapping
    fun getCurrencies(): ResponseEntity<RecordsWrapper<String>> {
        return ResponseEntity(RecordsWrapper(currencyProvider.getCurrencies()), HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteCurrency(@RequestParam code: String): ResponseEntity<Any> {
        try {
            if (!currencyProvider.isValidCode(code)) {
                throw CurrencyCodeIsInvalidException(code)
            }
            exchangeRateProvider.deleteRatesOf(code)
            currencyProvider.deleteCurrency(code)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (ex: Exception) {
            when (ex) {
                is NoCurrencyExistsException -> throw ParameterInvalidException("code", ex.message)
                is CurrencyCodeIsInvalidException -> throw ParameterInvalidException("code", ex.message)
                else -> throw UnknownException(ex)
            }
        }
    }
}