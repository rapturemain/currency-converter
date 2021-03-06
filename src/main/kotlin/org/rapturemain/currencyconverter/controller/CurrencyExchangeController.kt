package org.rapturemain.currencyconverter.controller

import org.rapturemain.currencyconverter.exception.CurrencyConverterApiException
import org.rapturemain.currencyconverter.exception.NoCurrencyExistsException
import org.rapturemain.currencyconverter.exception.NoExchangeRateException
import org.rapturemain.currencyconverter.exceptionhandler.InternalServerError
import org.rapturemain.currencyconverter.exceptionhandler.ParameterInvalidException
import org.rapturemain.currencyconverter.exceptionhandler.UnknownException
import org.rapturemain.currencyconverter.service.CurrencyExchangeRateProviderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.NumberFormatException
import java.math.BigDecimal

@RestController
@RequestMapping("/restapi/v1/exchange/")
class CurrencyExchangeController
@Autowired constructor(private val exchangeRateProvider: CurrencyExchangeRateProviderService){

    @GetMapping
    fun getExchangeRate(@RequestParam from: String, @RequestParam to: String, @RequestParam amount: String): ResponseEntity<Map<String, String>> {
        try {
            val resp = mutableMapOf<String, String>()
            val amountBD =  BigDecimal(amount)
            val rate = exchangeRateProvider.getRate(from, to)
            val amountTo = amountBD.multiply(rate)
            resp["${from}_${to}"] = rate.toString()
            resp["toAmount"] = amountTo.toString()
            return ResponseEntity(resp, HttpStatus.OK)
        } catch (ex: Exception) {
            when (ex) {
                is NoCurrencyExistsException -> throw ParameterInvalidException("'from' or 'to'", ex.message)
                is NoExchangeRateException -> throw ParameterInvalidException("'from' or 'to'", ex.message)
                is CurrencyConverterApiException -> throw InternalServerError(ex.message)
                is NumberFormatException -> throw ParameterInvalidException("amount", "Cannot convert [$amount] to number")
                else -> throw UnknownException(ex)
            }
        }
    }

}