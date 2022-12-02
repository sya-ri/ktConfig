package dev.s7a.ktconfig

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class PathName(val name: String)
