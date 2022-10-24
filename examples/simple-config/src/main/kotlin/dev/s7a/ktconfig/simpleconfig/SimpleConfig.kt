package dev.s7a.ktconfig.simpleconfig

import kotlinx.serialization.Serializable

@Serializable
data class SimpleConfig(
    val text: String = "default",
    val lines: List<String> = listOf()
)
