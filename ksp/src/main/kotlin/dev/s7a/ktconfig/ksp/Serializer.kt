package dev.s7a.ktconfig.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import dev.s7a.ktconfig.ksp.KtConfigAnnotation.Companion.getKtConfigAnnotation
import kotlin.sequences.forEach

sealed interface Serializer {
    data object ConfigurationSerializable : Serializer

    data class BuiltIn(
        val name: String,
    ) : Serializer {
        val serializerType = ClassName("dev.s7a.ktconfig.serializer", "${name}Serializer")
    }

    data class Collection(
        val name: String,
        val supportNullableValue: Boolean,
    ) : Serializer {
        val serializerType = ClassName("dev.s7a.ktconfig.serializer", "${name}Serializer")
    }

    data class Nested(
        val qualifiedName: String,
        val loaderType: ClassName,
    ) : Serializer

    data class Custom(
        val qualifiedName: String,
    ) : Serializer {
        val serializerType = ClassName(qualifiedName.substringBeforeLast('.'), qualifiedName.substringAfterLast('.'))
    }

    companion object {
        private val serializers =
            mapOf(
                // Primitive
                "kotlin.Byte" to BuiltIn("Byte"),
                "kotlin.Char" to BuiltIn("Char"),
                "kotlin.Int" to BuiltIn("Int"),
                "kotlin.Long" to BuiltIn("Long"),
                "kotlin.Short" to BuiltIn("Short"),
                "kotlin.String" to BuiltIn("String"),
                "kotlin.UByte" to BuiltIn("UByte"),
                "kotlin.UInt" to BuiltIn("UInt"),
                "kotlin.ULong" to BuiltIn("ULong"),
                "kotlin.UShort" to BuiltIn("UShort"),
                "kotlin.Double" to BuiltIn("Double"),
                "kotlin.Float" to BuiltIn("Float"),
                "kotlin.Boolean" to BuiltIn("Boolean"),
                // Common
                "java.util.UUID" to BuiltIn("UUID"),
                "java.time.Instant" to BuiltIn("Instant"),
                "java.time.LocalTime" to BuiltIn("LocalTime"),
                "java.time.LocalDate" to BuiltIn("LocalDate"),
                "java.time.LocalDateTime" to BuiltIn("LocalDateTime"),
                "java.time.Year" to BuiltIn("Year"),
                "java.time.YearMonth" to BuiltIn("YearMonth"),
                "java.time.OffsetTime" to BuiltIn("OffsetTime"),
                "java.time.OffsetDateTime" to BuiltIn("OffsetDateTime"),
                "java.time.ZonedDateTime" to BuiltIn("ZonedDateTime"),
                "java.time.Duration" to BuiltIn("Duration"),
                "java.time.Period" to BuiltIn("Period"),
                // Collections
                "kotlin.Array" to Collection("Array", true),
                "kotlin.ByteArray" to Collection("ByteArray", false),
                "kotlin.CharArray" to Collection("CharArray", false),
                "kotlin.IntArray" to Collection("IntArray", false),
                "kotlin.LongArray" to Collection("LongArray", false),
                "kotlin.ShortArray" to Collection("ShortArray", false),
                "kotlin.UByteArray" to Collection("UByteArray", false),
                "kotlin.UIntArray" to Collection("UIntArray", false),
                "kotlin.ULongArray" to Collection("ULongArray", false),
                "kotlin.UShortArray" to Collection("UShortArray", false),
                "kotlin.DoubleArray" to Collection("DoubleArray", false),
                "kotlin.FloatArray" to Collection("FloatArray", false),
                "kotlin.BooleanArray" to Collection("BooleanArray", false),
                "kotlin.collections.List" to Collection("List", true),
                "kotlin.collections.Set" to Collection("Set", true),
                "kotlin.collections.ArrayDeque" to Collection("ArrayDeque", true),
                "kotlin.collections.Map" to Collection("Map", true),
            )

        /**
         * Finds the appropriate serializer name for a given type.
         * First checks if the type implements ConfigurationSerializable,
         * then looks up built-in serializers.
         *
         * @param qualifiedName The fully qualified name of the type
         * @param type The KSType representing the type to find a serializer for
         * @return The name of the serializer to use, or null if no suitable serializer is found
         */
        fun findSerializer(
            qualifiedName: String,
            type: KSType,
        ): Serializer? {
            val declaration = type.declaration
            if (declaration is KSClassDeclaration) {
                // Check if type marked @KtConfig
                val ktConfig = declaration.getKtConfigAnnotation()
                if (ktConfig != null) {
                    return Nested(qualifiedName, ClassName(declaration.packageName.asString(), getLoaderName(declaration)))
                }

                // Check if type implements ConfigurationSerializable
                declaration.getAllSuperTypes().forEach { superType ->
                    val qualifiedName = superType.declaration.qualifiedName?.asString()
                    if (qualifiedName == "org.bukkit.configuration.serialization.ConfigurationSerializable") {
                        return ConfigurationSerializable
                    }
                }
            }

            // Lookup serializer name from the predefined map of built-in serializers
            return serializers[qualifiedName]
        }

        /**
         * Resolves a list of serializers by flattening nested serializers and removing duplicates.
         *
         * @return A flattened list of unique initializable serializers identified by their unique names that need initialization
         */
        fun List<Parameter.Serializer>.extractInitializableSerializers(): List<Parameter.Serializer.InitializableSerializer> =
            filterIsInstance<Parameter.Serializer.InitializableSerializer>()
                .flatMap {
                    when (it) {
                        is Parameter.Serializer.Class -> {
                            it.arguments.extractInitializableSerializers() + it
                        }

                        is Parameter.Serializer.Nested -> {
                            listOf(it)
                        }

                        is Parameter.Serializer.ConfigurationSerializableClass -> {
                            listOf(it)
                        }

                        is Parameter.Serializer.EnumClass -> {
                            listOf(it)
                        }

                        is Parameter.Serializer.ValueClass -> {
                            listOf(it.argument).extractInitializableSerializers() + it
                        }
                    }
                }.distinctBy(Parameter.Serializer::uniqueName)
    }
}
