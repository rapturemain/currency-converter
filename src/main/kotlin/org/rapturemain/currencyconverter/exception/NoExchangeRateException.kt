package org.rapturemain.currencyconverter.exception

import java.lang.Exception
import java.util.*

class NoExchangeRateException : Exception {
    constructor(currencyFrom: Currency, currencyTo: Currency) :
            super("${currencyFrom.currencyCode} to ${currencyTo.currencyCode} rate does not exist")
}