package com.github.tagwan.japm.internal


import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.util.*
import kotlin.reflect.KProperty


class PropertiesDelegate(private val path: String, private val defaultValu: String = "") {

    private lateinit var url: URL

    private val properties: Properties by lazy {
        val prop = Properties()
        url = try {
            javaClass.getResourceAsStream(path).use {
                prop.load(it)
            }
            javaClass.getResource(path)!!
        } catch (e: Exception) {
            try {
                ClassLoader.getSystemClassLoader().getResourceAsStream(path).use {
                    prop.load(it)
                }
                ClassLoader.getSystemClassLoader().getResource(path)!!
            } catch (e: Exception) {
                FileInputStream(path).use {
                    prop.load(it)
                }
                URL("file:///${File(path).canonicalPath}")  // file后面三个斜杠
            }
        }
        prop
    }

    operator fun getValue(thisRef: Any?, kProperty: KProperty<*>): String {
        return properties.getProperty(kProperty.name, defaultValu)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        properties.setProperty(property.name, value)
        File(url.toURI()).outputStream().use {
            properties.store(it, "")
        }
    }
}

abstract class AbsProperties(path: String) {
    protected val prop = PropertiesDelegate(path)
}

class Config : AbsProperties("japm-template.properties") {
    var name by prop
    var selfKey by prop
}

fun main() {

    val config = Config()
    //config[]
    println(config.name)
    println(config.name)
    println(config.selfKey)

}




