package com.github.tagwan.japm

import com.github.tagwan.japm.internal.PropertiesUtils
import org.slf4j.LoggerFactory


class Echo {
    fun echo(any: Any) {
        println("Echo::echo()-->$any")
    }
}


fun main() {
    val logger = LoggerFactory.getLogger(Echo::class.java)
    if (System.getProperty("JapmPropFile") == null) {
        logger.info("启动参数没有指定`JapmPropFile`， 启用默认配置")
        System.setProperty("JapmPropFile", "japm-template.properties")
    }
    val path = System.getProperty("JapmPropFile")
    val propertiesUtils = PropertiesUtils(path)
    val str = propertiesUtils.getProperty("collector1")

    Echo().echo(str)
}