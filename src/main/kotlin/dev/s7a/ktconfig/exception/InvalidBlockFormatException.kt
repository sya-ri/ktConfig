package dev.s7a.ktconfig.exception

class InvalidBlockFormatException(text: String) : InvalidFormatException(text, "%s,%d,%d,%d")
