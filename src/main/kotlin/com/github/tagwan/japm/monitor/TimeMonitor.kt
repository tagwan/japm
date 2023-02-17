package com.github.tagwan.japm.monitor

import com.github.tagwan.japm.mgr.ConfigMgr
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 执行时间
 *
 * @constructor Create empty Time monitor
 */
object TimeMonitor : AbstractMonitor(TimeMonitor::class), IMonitor {
    private val logger = LoggerFactory.getLogger(TimeMonitor::class.java)
    private val timeMonitorMap = ConcurrentHashMap<String, Long>()
    private var diff: Long = 0L

    override fun init2() {
        diff = ConfigMgr.metricsCfg.minTime
    }

    @InjectOrder(1)
    override fun injectOnStart(key: String) {
        val now = System.currentTimeMillis()
        timeMonitorMap[convertName(key)] = now
    }

    @InjectOrder(1)
    override fun injectOnOver(key: String) {
        val startTime = timeMonitorMap[convertName(key)]
            ?: return
        val useTime = System.currentTimeMillis() - startTime
        if (useTime < diff) {
            return
        }
        logger.info(" $key 执行耗时 ${useTime}ms")
    }

    private fun convertName(name: String) = "METRICS#TIME-${Thread.currentThread().id}-$name"
}

