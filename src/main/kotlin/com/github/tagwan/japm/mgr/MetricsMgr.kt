package com.github.tagwan.japm.mgr

import com.github.tagwan.japm.monitor.TimeMonitor

object MetricsMgr {
    fun init() {
        TimeMonitor.init()
    }
}