package dev.s7a.ktconfig.exception

open class InvalidFormatException(text: String, format: String) : IllegalArgumentException("Invalid format: $text (expected: $format)")
