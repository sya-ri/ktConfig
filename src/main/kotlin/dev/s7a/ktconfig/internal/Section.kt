package dev.s7a.ktconfig.internal

internal class Section(
    val values: List<Value>?,
) {
    data class Value(
        val name: String,
        val comments: List<String>?,
        val value: Any?,
    )
}
