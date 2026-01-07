package dev.s7a.ktconfig.ksp

sealed interface Discriminator {
    fun getAll(): List<String>

    data class Root(
        val value: String,
    ) : Discriminator {
        override fun getAll() = listOf(value)
    }

    data class Childs(
        val values: List<String>,
    ) : Discriminator {
        override fun getAll() = values
    }
}
