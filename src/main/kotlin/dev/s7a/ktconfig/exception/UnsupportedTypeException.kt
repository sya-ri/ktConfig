package dev.s7a.ktconfig.exception

import kotlin.reflect.KType

class UnsupportedTypeException(type: KType) : IllegalArgumentException("$type is unsupported")
