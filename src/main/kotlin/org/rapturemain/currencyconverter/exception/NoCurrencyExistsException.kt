package org.rapturemain.currencyconverter.exception

import java.lang.Exception

class NoCurrencyExistsException : Exception {
    constructor(currencyCode: String) : super("Currency $currencyCode does not exist")
}