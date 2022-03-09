package com.github.tagwan.japm.monitor

import kotlin.reflect.KClass

/**
 * 检测器基类
 *
 * @author jdg
 */
abstract class BaseMonitor(
    private val clazz: KClass<*>
) {

    lateinit var clazzName: String

    fun init() {
        TimeMonitor.clazzName = clazz.java.name.replace('.', '/')
        init2()
    }

    abstract fun init2()
}