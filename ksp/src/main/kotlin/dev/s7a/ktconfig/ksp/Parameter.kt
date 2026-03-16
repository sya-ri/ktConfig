package dev.s7a.ktconfig.ksp

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode

data class Parameter(
    val pathName: String,
    val name: String,
    val serializer: Serializer,
    val comment: List<String>?,
) {
    val isNullable
        get() = serializer.isNullable

    sealed class Serializer(
        val typeName: TypeName,
        val isNullable: Boolean,
    ) {
        abstract val uniqueName: String
        abstract val refKey: String
        abstract val ref: Any
        abstract val keyable: Boolean

        val getFn = if (isNullable) "get" else "getOrThrow"

        class Object(
            type: ClassName,
            isNullable: Boolean,
            name: String,
            serializerType: ClassName,
        ) : Serializer(type, isNullable) {
            override val uniqueName = name
            override val refKey = "%T"
            override val ref = serializerType
            override val keyable = true
        }

        sealed class InitializableSerializer(
            type: TypeName,
            isNullable: Boolean,
            name: String,
            protected val serializerType: ClassName = ClassName("dev.s7a.ktconfig.serializer", "${name}Serializer"),
        ) : Serializer(type, isNullable) {
            abstract val initialize: CodeBlock
            override val refKey = "%L"
        }

        // Properties like type, uniqueName, ref are stored as class properties
        // to avoid recalculating them each time they are accessed
        class Class(
            parentType: ClassName,
            isNullable: Boolean,
            name: String,
            serializerType: ClassName,
            val arguments: List<Serializer>,
            val nullableValue: Boolean,
        ) : InitializableSerializer(
                parentType.parameterizedBy(
                    arguments.mapIndexed { index, it ->
                        if (arguments.lastIndex == index) {
                            it.typeName.copy(nullable = nullableValue)
                        } else {
                            it.typeName
                        }
                    },
                ),
                isNullable,
                name,
                serializerType,
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
                buildCodeBlock {
                    add(
                        "%T(%L)",
                        if (nullableValue) serializerType.nestedClass("Nullable") else serializerType,
                        arguments.joinToCode {
                            buildCodeBlock {
                                add(it.refKey, it.ref)
                            }
                        },
                    )
                }
        }

        class Nested(
            type: ClassName,
            isNullable: Boolean,
            qualifiedName: String,
            loaderType: ClassName,
        ) : InitializableSerializer(type, isNullable, "Nested") {
            override val uniqueName = qualifiedName.replace('.', '_')
            override val ref = uniqueName
            override val keyable = false
            override val initialize =
                buildCodeBlock {
                    add("%T(%T)", serializerType, loaderType)
                }
        }

        class ConfigurationSerializableClass(
            type: ClassName,
            isNullable: Boolean,
        ) : InitializableSerializer(type, isNullable, "ConfigurationSerializable") {
            override val uniqueName = type.canonicalName.replace(".", "_")
            override val ref = uniqueName
            override val keyable = false
            override val initialize =
                buildCodeBlock {
                    add("%T<%T>()", serializerType, type)
                }
        }

        class EnumClass(
            type: ClassName,
            isNullable: Boolean,
        ) : InitializableSerializer(type, isNullable, "Enum") {
            override val uniqueName = type.canonicalName.replace(".", "_")
            override val ref = uniqueName
            override val keyable = true
            override val initialize =
                buildCodeBlock {
                    add("%T(%T::class.java)", serializerType, type)
                }
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
                buildCodeBlock {
                    add(
                        "%T(${argument.refKey}, { %T(it) }, { it.%L })",
                        if (keyable) serializerType.nestedClass("Keyable") else serializerType,
                        argument.ref,
                        type,
                        parameterName,
                    )
                }
        }
    }
}
