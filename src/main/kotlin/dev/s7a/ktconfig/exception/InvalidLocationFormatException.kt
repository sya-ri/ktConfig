package dev.s7a.ktconfig.exception

class InvalidLocationFormatException(text: String) : InvalidFormatException(text, "%s,%f,%f,%f or %s,%f,%f,%f,%f,%f")
