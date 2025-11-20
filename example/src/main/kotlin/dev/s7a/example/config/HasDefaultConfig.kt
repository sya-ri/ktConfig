package dev.s7a.example.config

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.UseDefault

@KtConfig
@UseDefault
data class HasDefaultConfig(
    val value: String = "default",
)
