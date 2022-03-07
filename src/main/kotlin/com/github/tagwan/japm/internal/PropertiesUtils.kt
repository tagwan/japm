package com.github.tagwan.japm.internal

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.FileReader
import java.util.*


object PropertiesUtils {

    private val logger = LoggerFactory.getLogger(PropertiesUtils::class.java)
    private var properties: Properties = Properties()

    fun init() {
        if (System.getProperty("JapmPropFile") == null) {
            logger.info("启动参数没有指定`JapmPropFile`， 启用默认配置")
            System.setProperty("JapmPropFile", "japm-template.properties")
        }
        val path = System.getProperty("JapmPropFile")
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


}