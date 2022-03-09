package com.github.tagwan.japm

import com.github.tagwan.japm.core.ApmTransformer
import com.github.tagwan.japm.mgr.ConfigMgr
import com.github.tagwan.japm.const.BANNER
import com.github.tagwan.japm.const.StateEnum
import com.github.tagwan.japm.core.MonitorClassVisitor
import com.github.tagwan.japm.internal.FileUtils
import com.github.tagwan.japm.mgr.MetricsMgr
import org.slf4j.LoggerFactory
import java.lang.instrument.Instrumentation

/**
 * Application
 *
 * @author jdg
 */
object Application {

    private val logger = LoggerFactory.getLogger(Application::class.java)
    var state: StateEnum = StateEnum.Prepare

    fun init() {
        // 当前的包名存一下
        ConfigMgr.init()
        MetricsMgr.init()
        state = StateEnum.Init
    }

    fun start(inst: Instrumentation) {
        state = StateEnum.Start
        logger.info(FileUtils.openText(this.javaClass.classLoader.getResource(BANNER)?.path ?: ""))
        val transformer = ApmTransformer()
        inst.addTransformer(transformer)
        state = StateEnum.Running
        logger.info("注入结束，本次共注入方法数::${MonitorClassVisitor.totals}")
    }

    fun exit() {
        state = StateEnum.Exit
        logger.error("JAPM发生异常导致了退出")
    }
}

fun main() {
    Application.init()
}