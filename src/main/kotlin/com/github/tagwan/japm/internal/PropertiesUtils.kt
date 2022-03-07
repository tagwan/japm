package com.github.tagwan.japm.internal

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.util.*


class PropertiesUtils(
    private val path: String
) {

    var properties: Properties = Properties()

    init {
        val bufferedReader = BufferedReader(FileReader(path))
        properties.load(bufferedReader)
    }

    fun getProperty(key: String): String {
        try {
            return properties.getProperty(key)
        } catch (e: Exception) {
            logger.error("key::$key 没有获取到配置值，请检查配置")
            return ""
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PropertiesUtils::class.java)
    }
}