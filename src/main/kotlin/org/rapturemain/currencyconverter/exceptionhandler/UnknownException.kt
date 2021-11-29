package org.rapturemain.currencyconverter.exceptionhandler

import java.lang.RuntimeException

class UnknownException(cause: Throwable) : RuntimeException(cause)