package org.rapturemain.currencyconverter.exceptionhandler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ParameterInvalidException::class)
    fun handleNoCurrencyExistsException(ex: ParameterInvalidException): ResponseEntity<MessageResponse> {
        return ResponseEntity<MessageResponse>(
                MessageResponse("Parameter [${ex.parameterName}] is invalid.${
                    if (ex.description != null) " ${ex.description}." else  ""}"),
                HttpStatus.BAD_REQUEST
        )
    }

    @ResponseBody
    @ExceptionHandler(UnknownException::class)
    fun handleUnknownException(ex: UnknownException): ResponseEntity<MessageResponse> {
        return ResponseEntity<MessageResponse>(
                MessageResponse("Internal Server Error."),
                HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ResponseBody
    @ExceptionHandler(InternalServerError::class)
    fun handleInternalServerError(ex: UnknownException): ResponseEntity<MessageResponse> {
        return ResponseEntity<MessageResponse>(
                MessageResponse("Internal Server Error.${
                    if (ex.message != null) " ${ex.message}." else  ""}"),
                HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}