package org.rapturemain.currencyconverter.controller

import org.rapturemain.currencyconverter.exception.InvalidRateException
import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.rapturemain.currencyconverter.exception.NoExchangeRateException
import org.rapturemain.currencyconverter.exceptionhandler.ParameterInvalidException
import org.rapturemain.currencyconverter.exceptionhandler.UnknownException
import org.rapturemain.currencyconverter.service.CurrencyExchangeRateProviderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/restapi/v1/exchange-rate/")
class CurrencyExchangeController
@Autowired constructor(private val exchangeRateProvider: CurrencyExchangeRateProviderService) {

    @GetMapping
    fun getExchangeRate(@RequestParam from: String, @RequestParam to: String): ResponseEntity<Map<String, String>> {
        try {
            val rate = exchangeRateProvider.getRate(from, to)
            return ResponseEntity(mapOf("${from}_${to}" to rate.toString()), HttpStatus.OK)
        } catch (ex: Exception) {
            when (ex) {
                is NoCurrencyExistsException -> throw ParameterInvalidException("'from' or 'to'", ex.message)
                is NoExchangeRateException -> throw ParameterInvalidException("'from' or 'to'", ex.message)
                else -> throw UnknownException(ex)
            }
        }
    }

    @PostMapping
    fun updateExchangeRate(@RequestParam from: String, @RequestParam to: String, @RequestParam rate: String): ResponseEntity<Any> {
        try {
            exchangeRateProvider.updateCache(from, to, rate)
            return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
        } catch (ex: Exception) {
            when (ex) {
                is NoCurrencyExistsException -> throw ParameterInvalidException("'from' or 'to'", ex.message)
                is InvalidRateException -> throw ParameterInvalidException("rate", ex.message)
                else -> throw UnknownException(ex)
            }
        }
    }
}