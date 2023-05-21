package dev.s7a.ktconfig.internal

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection

@JvmInline
internal value class ProjectionMap private constructor(private val map: Map<KTypeParameter, KTypeProjection>) {
    constructor(clazz: KClass<*>, type: KType): this(
        clazz.typeParameters.mapIndexed { index, parameter ->
            parameter to type.arguments[index]
        }.toMap()
    )

    fun type(typeParameter: KTypeParameter): KType {
        return map[typeParameter]!!.type!!
    }

    fun type(type: KType): KType {
        return map[type.classifier]?.type ?: type
    }

    fun typeArgument(type: KType, index: Int): KType {
        return type(type.arguments[index].type!!)
    }
}
