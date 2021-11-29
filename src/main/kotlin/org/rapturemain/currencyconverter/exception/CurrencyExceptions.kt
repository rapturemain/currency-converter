package org.rapturemain.currencyconverter.exception

class CurrencyAlreadyExistsException : Exception {
    constructor(currencyCode: String) : super("Currency $currencyCode already exists")
}

class CurrencyCodeIsInvalidException : Exception {
    constructor(currencyCode: String) : super("Currency [$currencyCode] is invalid. Must contain only A-Z letters")
}

class NoCurrencyExistsException : java.lang.Exception {
    constructor(currencyCode: String) : super("Currency $currencyCode does not exist")
}