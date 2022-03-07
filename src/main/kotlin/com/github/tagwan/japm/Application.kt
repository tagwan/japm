package com.github.tagwan.japm

import com.github.tagwan.japm.internal.PropertiesUtils
import org.slf4j.LoggerFactory


object Application {
    private val logger = LoggerFactory.getLogger(Application::class.java)

    fun init() {
        PropertiesUtils.init()
        printBanner()
    }

    fun printBanner() {
        val uri = this.javaClass.classLoader.getResource("banner.txt")
            ?: return
        logger.info(PropertiesUtils.openText(uri.path))
    }
}

fun main() {
    Application.init()
}