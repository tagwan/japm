package com.github.tagwan.japm.internal

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Collector(
    val name: String
)