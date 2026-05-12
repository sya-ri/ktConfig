import dev.s7a.ktconfig.KtConfig
import dev.s7a.ktconfig.SerialName
import dev.s7a.ktconfig.exception.KtConfigLoadException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

sealed interface TypeAliasSealedConfig {
    @KtConfig
    @SerialName("string")
    data class StringValue(
        val value: String,
    ) : TypeAliasSealedConfig

    @KtConfig
    @SerialName("int")
    data class IntValue(
        val value: Int,
    ) : TypeAliasSealedConfig
}

@KtConfig(discriminator = "type", loaderName = "TypeAliasSealedConfigLoader")
typealias TypeAliasSealedConfigAlias = TypeAliasSealedConfig

sealed interface UnannotatedChildTypeAliasSealedConfig

@SerialName("unannotated")
data class UnannotatedChildSealedValue(
    val value: String,
) : UnannotatedChildTypeAliasSealedConfig

@KtConfig(discriminator = "type", loaderName = "UnannotatedChildTypeAliasSealedConfigLoader")
typealias UnannotatedChildTypeAliasSealedConfigAlias = UnannotatedChildTypeAliasSealedConfig

sealed interface DuplicateSerializerChildTypeAliasSealedConfig

@SerialName("first-list")
data class FirstDuplicateSerializerChildSealedValue(
    val values: List<String>,
) : DuplicateSerializerChildTypeAliasSealedConfig

@SerialName("second-list")
data class SecondDuplicateSerializerChildSealedValue(
    val values: List<String>,
) : DuplicateSerializerChildTypeAliasSealedConfig

@KtConfig(discriminator = "type", loaderName = "DuplicateSerializerChildTypeAliasSealedConfigLoader")
typealias DuplicateSerializerChildTypeAliasSealedConfigAlias = DuplicateSerializerChildTypeAliasSealedConfig

sealed interface ExplicitDefaultChildTypeAliasSealedConfig

@KtConfig(hasDefault = true)
@SerialName("default-child")
data class ExplicitDefaultChildSealedValue(
    val value: String = "default",
) : ExplicitDefaultChildTypeAliasSealedConfig

@KtConfig(discriminator = "type", loaderName = "ExplicitDefaultChildTypeAliasSealedConfigLoader")
typealias ExplicitDefaultChildTypeAliasSealedConfigAlias = ExplicitDefaultChildTypeAliasSealedConfig

sealed interface ExplicitNonDefaultChildTypeAliasSealedConfig

@KtConfig(hasDefault = false)
@SerialName("non-default-child")
data class ExplicitNonDefaultChildSealedValue(
    val value: String = "default",
) : ExplicitNonDefaultChildTypeAliasSealedConfig

@KtConfig(discriminator = "type", hasDefault = true, loaderName = "ExplicitNonDefaultChildTypeAliasSealedConfigLoader")
typealias ExplicitNonDefaultChildTypeAliasSealedConfigAlias = ExplicitNonDefaultChildTypeAliasSealedConfig

sealed interface GenericExplicitDefaultChildTypeAliasSealedConfig<T>

@KtConfig(hasDefault = true)
@SerialName("generic-default-child")
data class GenericExplicitDefaultChildSealedValue<T>(
    val values: List<T> = emptyList(),
) : GenericExplicitDefaultChildTypeAliasSealedConfig<T>

@KtConfig(discriminator = "type", hasDefault = false, loaderName = "GenericExplicitDefaultChildTypeAliasSealedConfigLoader")
typealias GenericExplicitDefaultChildTypeAliasSealedConfigAlias = GenericExplicitDefaultChildTypeAliasSealedConfig<String>

sealed interface GenericExplicitNonDefaultChildTypeAliasSealedConfig<T>

@KtConfig(hasDefault = false)
@SerialName("generic-non-default-child")
data class GenericExplicitNonDefaultChildSealedValue<T>(
    val values: List<T> = emptyList(),
) : GenericExplicitNonDefaultChildTypeAliasSealedConfig<T>

@KtConfig(discriminator = "type", hasDefault = true, loaderName = "GenericExplicitNonDefaultChildTypeAliasSealedConfigLoader")
typealias GenericExplicitNonDefaultChildTypeAliasSealedConfigAlias = GenericExplicitNonDefaultChildTypeAliasSealedConfig<String>

sealed interface GenericTypeAliasSealedConfig<T>

@SerialName("generic-string")
data class GenericSealedValue<T>(
    val value: T,
) : GenericTypeAliasSealedConfig<T>

@KtConfig(loaderName = "StringGenericSealedValueLoader")
typealias StringGenericSealedValue = GenericSealedValue<String>

@KtConfig(discriminator = "type", loaderName = "StringGenericTypeAliasSealedConfigLoader")
typealias StringGenericTypeAliasSealedConfig = GenericTypeAliasSealedConfig<String>

@KtConfig
sealed interface DeferredGenericTypeAliasSealedConfig<T>

@SerialName("deferred-generic")
data class DeferredGenericSealedValue<T>(
    val value: T,
) : DeferredGenericTypeAliasSealedConfig<T>

@KtConfig(discriminator = "type", loaderName = "DeferredStringGenericTypeAliasSealedConfigLoader")
typealias DeferredStringGenericTypeAliasSealedConfig = DeferredGenericTypeAliasSealedConfig<String>

sealed interface PairGenericTypeAliasSealedConfig<K, V>

@SerialName("pair")
data class PairGenericSealedValue<K, V>(
    val key: K,
    val value: V,
) : PairGenericTypeAliasSealedConfig<K, V>

@KtConfig(loaderName = "StringIntPairGenericSealedValueLoader")
typealias StringIntPairGenericSealedValue = PairGenericSealedValue<String, Int>

@KtConfig(discriminator = "type", loaderName = "StringIntPairGenericTypeAliasSealedConfigLoader")
typealias StringIntPairGenericTypeAliasSealedConfig = PairGenericTypeAliasSealedConfig<String, Int>

sealed interface SwappedGenericTypeAliasSealedConfig<K, V>

@SerialName("swapped")
data class SwappedGenericSealedValue<K, V>(
    val key: K,
    val value: V,
) : SwappedGenericTypeAliasSealedConfig<V, K>

@KtConfig(loaderName = "StringIntSwappedGenericSealedValueLoader")
typealias StringIntSwappedGenericSealedValue = SwappedGenericSealedValue<String, Int>

@KtConfig(discriminator = "type", loaderName = "IntStringSwappedGenericTypeAliasSealedConfigLoader")
typealias IntStringSwappedGenericTypeAliasSealedConfig = SwappedGenericTypeAliasSealedConfig<Int, String>

sealed interface NestedGenericTypeAliasSealedConfig<T>

@SerialName("nested")
data class NestedGenericSealedValue<T>(
    val values: List<T>,
) : NestedGenericTypeAliasSealedConfig<List<T>>

@KtConfig(loaderName = "NestedStringGenericSealedValueLoader")
typealias NestedStringGenericSealedValue = NestedGenericSealedValue<String>

@KtConfig(discriminator = "type", loaderName = "NestedStringGenericTypeAliasSealedConfigLoader")
typealias NestedStringGenericTypeAliasSealedConfig = NestedGenericTypeAliasSealedConfig<List<String>>

sealed interface NullableGenericTypeAliasSealedConfig<T>

@SerialName("nullable")
data class NullableGenericSealedValue<T>(
    val value: T?,
) : NullableGenericTypeAliasSealedConfig<T?>

@KtConfig(loaderName = "NullableStringGenericSealedValueLoader")
typealias NullableStringGenericSealedValue = NullableGenericSealedValue<String>

@KtConfig(discriminator = "type", loaderName = "NullableStringGenericTypeAliasSealedConfigLoader")
typealias NullableStringGenericTypeAliasSealedConfig = NullableGenericTypeAliasSealedConfig<String?>

sealed interface DeepGenericTypeAliasSealedConfig<T> {
    sealed interface Nested<T> : DeepGenericTypeAliasSealedConfig<T>
}

@SerialName("deep")
data class DeepGenericSealedValue<T>(
    val value: T,
) : DeepGenericTypeAliasSealedConfig.Nested<T>

@KtConfig(loaderName = "DeepStringGenericSealedValueLoader")
typealias DeepStringGenericSealedValue = DeepGenericSealedValue<String>

@KtConfig(discriminator = "type", loaderName = "DeepStringGenericTypeAliasSealedConfigLoader")
typealias DeepStringGenericTypeAliasSealedConfig = DeepGenericTypeAliasSealedConfig<String>

sealed interface UpperBoundGenericTypeAliasSealedConfig<T : Number>

@SerialName("upper")
data class UpperBoundGenericSealedValue<T : Number>(
    val value: T,
) : UpperBoundGenericTypeAliasSealedConfig<T>

@KtConfig(loaderName = "UpperBoundIntGenericSealedValueLoader")
typealias UpperBoundIntGenericSealedValue = UpperBoundGenericSealedValue<Int>

@KtConfig(discriminator = "type", loaderName = "UpperBoundIntGenericTypeAliasSealedConfigLoader")
typealias UpperBoundIntGenericTypeAliasSealedConfig = UpperBoundGenericTypeAliasSealedConfig<Int>

sealed interface DeepNestedGenericTypeAliasSealedConfig<T>

@SerialName("deep-nested")
data class DeepNestedGenericSealedValue<T>(
    val values: List<T?>?,
) : DeepNestedGenericTypeAliasSealedConfig<List<T?>?>

@KtConfig(loaderName = "DeepNestedStringGenericSealedValueLoader")
typealias DeepNestedStringGenericSealedValue = DeepNestedGenericSealedValue<String>

@KtConfig(discriminator = "type", loaderName = "DeepNestedStringGenericTypeAliasSealedConfigLoader")
typealias DeepNestedStringGenericTypeAliasSealedConfig = DeepNestedGenericTypeAliasSealedConfig<List<String?>?>

sealed interface MultipleAliasGenericTypeAliasSealedConfig<T>

@SerialName("multiple")
data class MultipleAliasGenericSealedValue<T>(
    val value: T,
) : MultipleAliasGenericTypeAliasSealedConfig<T>

@KtConfig(loaderName = "MultipleStringGenericSealedValueLoader")
typealias MultipleStringGenericSealedValue = MultipleAliasGenericSealedValue<String>

@KtConfig(loaderName = "MultipleIntGenericSealedValueLoader")
typealias MultipleIntGenericSealedValue = MultipleAliasGenericSealedValue<Int>

@KtConfig(discriminator = "type", loaderName = "MultipleStringGenericTypeAliasSealedConfigLoader")
typealias MultipleStringGenericTypeAliasSealedConfig = MultipleAliasGenericTypeAliasSealedConfig<String>

sealed interface NestedClassGenericTypeAliasSealedConfig<T> {
    @SerialName("nested-class")
    data class Child<T>(
        val value: T,
    ) : NestedClassGenericTypeAliasSealedConfig<T>
}

@KtConfig(loaderName = "NestedClassStringGenericSealedValueLoader")
typealias NestedClassStringGenericSealedValue = NestedClassGenericTypeAliasSealedConfig.Child<String>

@KtConfig(discriminator = "type", loaderName = "NestedClassStringGenericTypeAliasSealedConfigLoader")
typealias NestedClassStringGenericTypeAliasSealedConfig = NestedClassGenericTypeAliasSealedConfig<String>

class TypeAliasSealedKtConfigTest {
    @Test
    fun testGeneratedLoaderLoadsSealedSubtypeThroughTypeAlias() {
        assertEquals(
            TypeAliasSealedConfig.StringValue("value"),
            TypeAliasSealedConfigLoader.loadFromString(
                """
                type: string
                value: value
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun testGeneratedLoaderSavesSealedSubtypeThroughTypeAlias() {
        assertEquals(
            """
            type: int
            value: '1'
            
            """.trimIndent(),
            TypeAliasSealedConfigLoader.saveToString(TypeAliasSealedConfig.IntValue(1)),
        )
    }

    @Test
    fun testGeneratedLoaderLoadsUnannotatedSealedSubtypeThroughTypeAlias() {
        assertEquals(
            UnannotatedChildSealedValue("value"),
            UnannotatedChildTypeAliasSealedConfigLoader.loadFromString(
                """
                type: unannotated
                value: value
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun testGeneratedLoaderSavesUnannotatedSealedSubtypeThroughTypeAlias() {
        assertEquals(
            """
            type: unannotated
            value: value
            
            """.trimIndent(),
            UnannotatedChildTypeAliasSealedConfigLoader.saveToString(UnannotatedChildSealedValue("value")),
        )
    }

    @Test
    fun testGeneratedLoaderSharesSerializerPropertyAcrossSyntheticSealedSubtypeLoaders() {
        assertEquals(
            """
            type: second-list
            values:
            - a
            - b
            
            """.trimIndent(),
            DuplicateSerializerChildTypeAliasSealedConfigLoader.saveToString(
                SecondDuplicateSerializerChildSealedValue(listOf("a", "b")),
            ),
        )
    }

    @Test
    fun testExplicitSealedSubtypeAnnotationControlsDefaultValueThroughTypeAlias() {
        assertEquals(
            ExplicitDefaultChildSealedValue("default"),
            ExplicitDefaultChildTypeAliasSealedConfigLoader.loadFromString("type: default-child"),
        )
    }

    @Test
    fun testExplicitSealedSubtypeAnnotationDoesNotInheritParentDefaultValueThroughTypeAlias() {
        val exception = assertFailsWith<KtConfigLoadException> {
            ExplicitNonDefaultChildTypeAliasSealedConfigLoader.loadFromString("type: non-default-child")
        }

        assertEquals(
            """
            Failed to load config (1 error):
            - [value] Not found value
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testExplicitGenericSealedSubtypeAnnotationControlsDefaultValueThroughTypeAlias() {
        assertEquals(
            GenericExplicitDefaultChildSealedValue(emptyList()),
            GenericExplicitDefaultChildTypeAliasSealedConfigLoader.loadFromString("type: generic-default-child"),
        )
    }

    @Test
    fun testExplicitGenericSealedSubtypeAnnotationDoesNotInheritParentDefaultValueThroughTypeAlias() {
        val exception = assertFailsWith<KtConfigLoadException> {
            GenericExplicitNonDefaultChildTypeAliasSealedConfigLoader.loadFromString("type: generic-non-default-child")
        }

        assertEquals(
            """
            Failed to load config (1 error):
            - [values] Not found value
            """.trimIndent(),
            exception.message,
        )
    }

    @Test
    fun testGeneratedLoaderLoadsGenericSealedSubtypeThroughTypeAlias() {
        assertEquals(
            StringGenericSealedValue("value"),
            StringGenericTypeAliasSealedConfigLoader.loadFromString(
                """
                type: generic-string
                value: value
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun testGeneratedLoaderSavesGenericSealedSubtypeThroughTypeAlias() {
        assertEquals(
            """
            type: generic-string
            value: value
            
            """.trimIndent(),
            StringGenericTypeAliasSealedConfigLoader.saveToString(StringGenericSealedValue("value")),
        )
    }

    @Test
    fun testGeneratedLoaderIsDeferredUntilGenericSealedTypeAliasIsConcrete() {
        assertEquals(
            """
            type: deferred-generic
            value: value
            
            """.trimIndent(),
            DeferredStringGenericTypeAliasSealedConfigLoader.saveToString(DeferredGenericSealedValue("value")),
        )
    }

    @Test
    fun testGeneratedLoaderUsesMultipleGenericArgumentsThroughTypeAlias() {
        assertEquals(
            """
            type: pair
            key: key
            value: '1'
            
            """.trimIndent(),
            StringIntPairGenericTypeAliasSealedConfigLoader.saveToString(StringIntPairGenericSealedValue("key", 1)),
        )
    }

    @Test
    fun testGeneratedLoaderUsesSwappedGenericArgumentsThroughTypeAlias() {
        assertEquals(
            """
            type: swapped
            key: key
            value: '1'
            
            """.trimIndent(),
            IntStringSwappedGenericTypeAliasSealedConfigLoader.saveToString(StringIntSwappedGenericSealedValue("key", 1)),
        )
    }

    @Test
    fun testGeneratedLoaderUsesNestedGenericArgumentThroughTypeAlias() {
        assertEquals(
            """
            type: nested
            values:
            - a
            - b
            
            """.trimIndent(),
            NestedStringGenericTypeAliasSealedConfigLoader.saveToString(NestedStringGenericSealedValue(listOf("a", "b"))),
        )
    }

    @Test
    fun testGeneratedLoaderUsesNullableGenericArgumentThroughTypeAlias() {
        assertEquals(
            NullableStringGenericSealedValue(null),
            NullableStringGenericTypeAliasSealedConfigLoader.loadFromString(
                """
                type: nullable
                value: null
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun testGeneratedLoaderUsesDeepSealedGenericSubtypeThroughTypeAlias() {
        assertEquals(
            """
            type: deep
            value: value
            
            """.trimIndent(),
            DeepStringGenericTypeAliasSealedConfigLoader.saveToString(DeepStringGenericSealedValue("value")),
        )
    }

    @Test
    fun testGeneratedLoaderUsesUpperBoundGenericArgumentThroughTypeAlias() {
        assertEquals(
            """
            type: upper
            value: '1'
            
            """.trimIndent(),
            UpperBoundIntGenericTypeAliasSealedConfigLoader.saveToString(UpperBoundIntGenericSealedValue(1)),
        )
    }

    @Test
    fun testGeneratedLoaderUsesDeepNestedNullableGenericArgumentThroughTypeAlias() {
        assertEquals(
            DeepNestedStringGenericSealedValue(listOf("a", null)),
            DeepNestedStringGenericTypeAliasSealedConfigLoader.loadFromString(
                """
                type: deep-nested
                values:
                - a
                - null
                """.trimIndent(),
            ),
        )
    }

    @Test
    fun testGeneratedLoaderSelectsMatchingChildAliasWhenMultipleAliasesExist() {
        assertEquals(
            """
            type: multiple
            value: value
            
            """.trimIndent(),
            MultipleStringGenericTypeAliasSealedConfigLoader.saveToString(MultipleStringGenericSealedValue("value")),
        )
    }

    @Test
    fun testGeneratedLoaderUsesNestedClassSubtypeThroughTypeAlias() {
        assertEquals(
            """
            type: nested-class
            value: value
            
            """.trimIndent(),
            NestedClassStringGenericTypeAliasSealedConfigLoader.saveToString(NestedClassStringGenericSealedValue("value")),
        )
    }
}
