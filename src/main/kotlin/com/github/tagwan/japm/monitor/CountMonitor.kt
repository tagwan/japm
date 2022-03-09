package com.github.tagwan.japm.monitor

object CountMonitor : BaseMonitor(CountMonitor::class), IMonitor {

    override fun init2() {
        // pass
    }

    @InjectOrder
    override fun injectOnStart(key: String) {
        //pass
    }

    @InjectOrder
    override fun injectOnOver(key: String) {
        //pass
    }

}