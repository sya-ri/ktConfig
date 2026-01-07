package dev.s7a.ktconfig.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

data class Parameter(
    val pathName: String,
    val name: String,
    val serializer: Serializer,
    val comment: List<String>?,
) {
    val isNullable
        get() = serializer.isNullable

    sealed class Serializer(
        val type: TypeName,
        val isNullable: Boolean,
    ) {
        abstract val uniqueName: String
        abstract val ref: String
        abstract val keyable: Boolean

        val getFn = if (isNullable) "get" else "getOrThrow"

        class Object(
            type: ClassName,
            isNullable: Boolean,
            name: String,
            qualifiedName: String,
        ) : Serializer(type, isNullable) {
            override val uniqueName = name
            override val ref = qualifiedName
            override val keyable = true
        }

        sealed class InitializableSerializer(
            type: TypeName,
            isNullable: Boolean,
            name: String,
            protected val qualifiedName: String = "dev.s7a.ktconfig.serializer.${name}Serializer",
        ) : Serializer(type, isNullable) {
            abstract val initialize: String
        }

        // Properties like type, uniqueName, ref are stored as class properties
        // to avoid recalculating them each time they are accessed
        class Class(
            parentType: ClassName,
            isNullable: Boolean,
            name: String,
            qualifiedName: String,
            val arguments: List<Serializer>,
            val nullableValue: Boolean,
        ) : InitializableSerializer(
                parentType.parameterizedBy(
                    arguments.mapIndexed { index, it ->
                        if (arguments.lastIndex == index) {
                            it.type.copy(nullable = nullableValue)
                        } else {
                            it.type
                        }
                    },
                ),
                isNullable,
                name,
                qualifiedName,
            ) {
            override val uniqueName =
                buildString {
                    if (nullableValue) append("Nullable")
                    append(name)
                    append("Of")
                    arguments.forEach {
                        append(it.uniqueName)
                    }
                }
            override val ref = uniqueName
            override val keyable = false
            override val initialize =
                buildString {
                    append(qualifiedName)
                    if (nullableValue) {
                        append(".Nullable")
                    }
                    append("(${arguments.joinToString(", ") { it.ref }})")
                }
        }

        class Nested(
            type: ClassName,
            isNullable: Boolean,
            qualifiedName: String,
            loaderName: String,
        ) : InitializableSerializer(type, isNullable, "Nested") {
            override val uniqueName = qualifiedName.replace('.', '_')
            override val ref = uniqueName
            override val keyable = false
            override val initialize = "${super.qualifiedName}($loaderName)"
        }

        class ConfigurationSerializableClass(
            type: ClassName,
            isNullable: Boolean,
            classQualifiedName: String,
        ) : InitializableSerializer(type, isNullable, "ConfigurationSerializable") {
            override val uniqueName = type.canonicalName.replace(".", "_")
            override val ref = uniqueName
            override val keyable = false
            override val initialize = "$qualifiedName<$classQualifiedName>()"
        }

        class EnumClass(
            type: ClassName,
            isNullable: Boolean,
            enumQualifiedName: String,
        ) : InitializableSerializer(type, isNullable, "Enum") {
            override val uniqueName = type.canonicalName.replace(".", "_")
            override val ref = uniqueName
            override val keyable = true
            override val initialize = "$qualifiedName($enumQualifiedName::class.java)"
        }

        class ValueClass(
            type: ClassName,
            isNullable: Boolean,
            parameterName: String,
            val argument: Serializer,
        ) : InitializableSerializer(type, isNullable, "Value") {
            override val uniqueName = type.canonicalName.replace(".", "_")
            override val ref = uniqueName
            override val keyable = argument.keyable
            override val initialize =
                buildString {
                    append(qualifiedName)
                    if (keyable) append(".Keyable")
                    append("(${argument.ref}, { $type(it) }, { it.$parameterName })")
                }
        }
    }
}
