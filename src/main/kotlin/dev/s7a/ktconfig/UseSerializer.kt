package dev.s7a.ktconfig

import kotlin.reflect.KClass

@Target(AnnotationTarget.TYPE)
annotation class UseSerializer(val with: KClass<out KtConfigSerializer>)
