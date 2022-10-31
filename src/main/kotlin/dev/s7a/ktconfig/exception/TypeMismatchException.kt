package dev.s7a.ktconfig.exception

import kotlin.reflect.KType

class TypeMismatchException(type: KType, value: Any?) : IllegalArgumentException("Expected $type, but ${if (value != null) "${value::class.qualifiedName}" else "null"}")
