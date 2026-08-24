package dev.s7a.ktconfig.fabric.minecraft

import dev.s7a.ktconfig.exception.InvalidFormatException

internal fun String.coordinates(size: Int): List<String> =
    split(',').map(String::trim).also {
        if (it.size != size) {
            throw InvalidFormatException(this, "$size comma-separated coordinates")
        }
    }
