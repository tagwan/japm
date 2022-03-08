package com.github.tagwan.japm.internal

import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSuperclassOf

class PropertiesDelegate(
    private val path: String,
    private val prefix: String = ""
) {

    private val properties: Properties by lazy {
        val prop = Properties()
        try {
            javaClass.getResourceAsStream(path).use {
                prop.load(it)
            }
        } catch (e: Exception) {
            try {
                ClassLoader.getSystemClassLoader().getResourceAsStream(path).use {
                    prop.load(it)
                }
            } catch (e: Exception) {
                FileInputStream(path).use {
                    prop.load(it)
                }
            }
        }

        prop
    }

    @SuppressWarnings("unchecked")
    operator fun <T> getValue(thisRef: Any, property: KProperty<*>): T {
        val value = properties["$prefix${property.name}"]
        val classOfT = property.returnType.classifier as KClass<*>
        return if (Number::class.isSuperclassOf(classOfT)) {
            classOfT.javaObjectType.getDeclaredMethod("parse${classOfT.simpleName}", String::class.java).invoke(null, value)
        } else {
            value
        } as T
    }

    operator fun <T> setValue(thisRef: Any, property: KProperty<*>, value: T) {
        properties["$prefix${property.name}"] = value
        File(path).outputStream().use {
            properties.store(it, "")
        }
    }
}