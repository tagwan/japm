package com.github.tagwan.japm

import com.github.tagwan.japm.agent.ApmTransformer
import com.github.tagwan.japm.const.BANNER
import com.github.tagwan.japm.const.StateEnum
import com.github.tagwan.japm.internal.PropertiesUtils
import com.github.tagwan.japm.monitor.TimeMonitor
import com.github.tagwan.japm.monitor.TimeMonitorClassVisitor
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
        PropertiesUtils.init()
        TimeMonitor.init()
        state = StateEnum.Init
    }

    fun start(inst: Instrumentation) {
        state = StateEnum.Start
        logger.info(PropertiesUtils.openText(this.javaClass.classLoader.getResource(BANNER)?.path ?: ""))
        val transformer = ApmTransformer()
        inst.addTransformer(transformer)
        state = StateEnum.Running
    }

    fun exit() {
        state = StateEnum.Exit
        logger.error("JAPM发生异常导致了退出")
    }
}

fun main() {
    Application.init()
}