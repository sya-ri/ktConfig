package dev.s7a.example.config

import dev.s7a.ktconfig.ForKtConfig

@ForKtConfig
data class SerializerTestConfig(
    val byte: Byte,
    val char: Char,
    val double: Double,
    val float: Float,
    val int: Int,
    val long: Long,
    val number: Number,
    val short: Short,
    val string: String,
    val uByte: UByte,
    val uInt: UInt,
    val uLong: ULong,
    val uShort: UShort,
)
