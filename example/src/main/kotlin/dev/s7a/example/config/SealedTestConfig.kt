package dev.s7a.example.config

import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.SerialName

@KtConfig
sealed interface SealedTestConfig {
    @KtConfig
    data class A(
        val a: String,
        val value: Int,
        val enum: Enum,
    ) : SealedTestConfig {
        enum class Enum {
            TestA,
        }
    }

    @KtConfig(discriminator = "type")
    sealed interface B : SealedTestConfig {
        @KtConfig
        @SerialName("b1")
        data class B1(
            val b1: String,
            val enum: Enum,
        ) : B {
            enum class Enum {
                TestB1,
            }
        }

        @KtConfig
        data class B2(
            val b2: String,
        ) : B
    }

    @KtConfig(discriminator = "") // -> $
    sealed class C : SealedTestConfig {
        @KtConfig
        data class C1(
            val c1: String,
        ) : C()
    }

    @KtConfig
    data class Ignored(
        val ignored: String,
    )
}
