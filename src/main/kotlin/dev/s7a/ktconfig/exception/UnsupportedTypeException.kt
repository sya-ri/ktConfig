package dev.s7a.ktconfig.exception

import kotlin.reflect.KType

class UnsupportedTypeException(type: KType, forName: String) : IllegalArgumentException("$type is unsupported for $forName")
