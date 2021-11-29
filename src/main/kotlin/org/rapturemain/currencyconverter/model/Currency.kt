package org.rapturemain.currencyconverter.model

import java.util.Currency

data class Currency(val currencyCode: String, val javaCurrency: Currency? = null)