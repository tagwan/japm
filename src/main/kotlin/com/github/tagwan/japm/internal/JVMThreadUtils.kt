package com.github.tagwan.japm.internal

import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean


/**
 * JVM 线程信息工具类
 *
 * @constructor Create empty J v m thread utils
 */
object JVMThreadUtils {
    private val threadMXBean: ThreadMXBean = ManagementFactory.getThreadMXBean()

    // Daemon线程总量
    val daemonThreadCount: Int
        get() = threadMXBean.daemonThreadCount

    // 当前线程总量
    val threadCount: Int
        get() = threadMXBean.threadCount

    // 死锁线程总量
    val deadLockedThreadCount: Int
        get() = try {
            val deadLockedThreadIds = threadMXBean.findDeadlockedThreads()
            deadLockedThreadIds?.size ?: 0
        } catch (e: Exception) {
            throw IllegalStateException(e.message, e)
        }

}
