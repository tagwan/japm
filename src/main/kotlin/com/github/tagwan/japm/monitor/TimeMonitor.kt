package com.github.tagwan.japm.monitor

import java.util.concurrent.ConcurrentHashMap

object TimeMonitor {

    val TAG =  "TIME_"
    val timeMonitorMap = ConcurrentHashMap<String, Long>()

    fun start(key: String) {
        val now  = System.currentTimeMillis()
        timeMonitorMap["${Thread.currentThread().id}-$key"] = now
    }

    fun end(key: String) {
        val startTime = timeMonitorMap["${Thread.currentThread().id}-$key"]
            ?: return

        val useTime = System.currentTimeMillis() - startTime


        println("key:${key} 执行耗时 ${useTime}ms")
;    }
}

