package dev.s7a.ktconfig.internal

class Section(
    val values: Map<String, Value>?,
) {
    data class Value(
        val comments: List<String>?,
        val value: Any?,
    )
}
