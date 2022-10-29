package dev.s7a.ktconfig

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.getFloat(path: String): Float? {
    val value = get(path)
    return if (value is Number) value.toFloat() else null
}

fun ConfigurationSection.getChar(path: String): Char? {
    return when (val value = get(path)) {
        is Char -> value
        is String -> value.singleOrNull()
        is Int -> value.toChar()
        else -> null
    }
}

fun ConfigurationSection.getByte(path: String): Byte? {
    return when (val value = get(path)) {
        is Byte -> value
        is String -> java.lang.Byte.valueOf(value)
        is Char -> value.code.toByte()
        is Number -> value.toByte()
        else -> null
    }
}

fun ConfigurationSection.getShort(path: String): Short? {
    val value = get(path)
    return if (value is Number) value.toShort() else null
}
