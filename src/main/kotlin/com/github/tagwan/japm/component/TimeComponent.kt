package com.github.tagwan.japm.component

import com.github.tagwan.japm.internal.PropertiesUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

object TimeComponent {
    private val logger = LoggerFactory.getLogger(TimeComponent::class.java)
    private val timeMonitorMap = ConcurrentHashMap<String, Long>()
    private val fnConvertName: (name: String) -> String = { key -> "TIME-${Thread.currentThread().id}-$key" }
    private var diff: Long = 0L
    fun init() {
        diff = PropertiesUtils.getProperty("collect.minTime").toLong()
    }

    fun start(key: String) {
        val now = System.currentTimeMillis()
        timeMonitorMap[fnConvertName(key)] = now
    }

    fun end(key: String) {
        val startTime = timeMonitorMap[fnConvertName(key)]
            ?: return
        val useTime = System.currentTimeMillis() - startTime
        if (useTime < diff) {
            return
        }
        logger.info("key:${key} 执行耗时 ${useTime}ms")
    }
}

