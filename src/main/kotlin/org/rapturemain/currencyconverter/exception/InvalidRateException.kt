package org.rapturemain.currencyconverter.exception

import java.lang.Exception

class InvalidRateException(rate: String) : Exception("Rate [$rate] is invalid")