package com.github.tagwan.test

import com.github.tagwan.japm.monitor.TimeMonitor

class Main {
    fun test() {
        TimeMonitor.start("123")
        TimeMonitor.end("123")
    }
}