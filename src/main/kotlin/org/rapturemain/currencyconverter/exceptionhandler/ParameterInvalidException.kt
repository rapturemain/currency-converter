package org.rapturemain.currencyconverter.exceptionhandler

import java.lang.RuntimeException

data class ParameterInvalidException(val parameterName: String, val description: String? = null) : RuntimeException()
