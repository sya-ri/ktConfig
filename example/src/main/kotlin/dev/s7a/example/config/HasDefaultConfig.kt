package dev.s7a.example.config

import dev.s7a.ktconfig.KtConfig

@KtConfig(hasDefault = true)
data class HasDefaultConfig(
    val value: String = "default",
)
