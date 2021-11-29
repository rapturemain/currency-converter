package org.rapturemain.currencyconverter.exception

import org.rapturemain.currencyconverter.model.Currency
import java.lang.Exception

class NoExchangeRateException : Exception {
    constructor(currencyFrom: Currency, currencyTo: Currency) :
            super("${currencyFrom.currencyCode} to ${currencyTo.currencyCode} rate does not exist")
}