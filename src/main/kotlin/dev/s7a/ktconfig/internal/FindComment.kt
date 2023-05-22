package dev.s7a.ktconfig.internal

import dev.s7a.ktconfig.Comment
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.findAnnotation

internal fun KAnnotatedElement.findComment(): List<String>? {
    return findAnnotation<Comment>()?.lines?.toList()
}
