package org.rapturemain.currencyconverter.exceptionhandler

import java.lang.RuntimeException

class InternalServerError(message: String? = null) : RuntimeException(message)