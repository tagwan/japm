package com.github.tagwan.japm.monitor

import org.slf4j.LoggerFactory

/**
 * 方法调用次数
 *
 * @constructor Create empty Count monitor
 */
object InvokeCountMonitor : AbstractMonitor(InvokeCountMonitor::class), IMonitor {

    private val logger = LoggerFactory.getLogger(TimeMonitor::class.java)
    private val countMonitorMap = HashMap<String, Int>()

    override fun init2() {
        // pass
    }

    /**
     * Inject on start
     *
     *  TODO 次数展示
     *
     * @param key
     */
    @InjectOrder
    override fun injectOnStart(key: String) {
        val count = countMonitorMap[key] ?: 0
        countMonitorMap[key] = count + 1
    }

    @InjectOrder
    override fun injectOnOver(key: String) {
        //pass
    }

}