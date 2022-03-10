package com.github.tagwan.japm.mgr

import org.slf4j.LoggerFactory

object SystemMgr {

    private val properties = System.getProperties()
    private val logger = LoggerFactory.getLogger(SystemMgr::class.java)

    fun init() {

        logger.info("os.name: ${properties.getProperty("os.name")}, " +
            "java.version: ${properties.getProperty("java.version")}, " +
            "java.home: ${properties.getProperty("java.home")}"
        )
    }
}