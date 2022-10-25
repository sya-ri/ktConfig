package dev.s7a.ktconfig.exception

open class InvalidFormatException(text: String) : IllegalArgumentException("For input string: $text")
