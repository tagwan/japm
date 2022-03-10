package com.github.tagwan.japm.internal

import java.lang.management.ManagementFactory


/**
 * JVM信息工具类
 *
 * @constructor Create empty J v m info utils
 */
object JVMInfoUtils {
    private val runtime = ManagementFactory.getRuntimeMXBean()
    private val classLoad = ManagementFactory.getClassLoadingMXBean()

    // 可能为null
    private val compilation = ManagementFactory.getCompilationMXBean()
    private val systemProperty = System.getProperties()

    /**
     * 获取JVM进程PID
     * @return
     */
    val pID: String
        get() {
            var pid = System.getProperty("pid")
            if (pid == null) {
                val name = runtime.name
                if (name != null) {
                    pid = name.split("@").toTypedArray()[0]
                    System.setProperty("pid", pid)
                }
            }
            return pid
        }

    /**
     * 获取JVM规范名称
     * @return
     */
    val jVMSpecName: String
        get() = runtime.specName

    /**
     * 获取JVM规范运营商
     * @return
     */
    val jVMSpecVendor: String
        get() = runtime.specVendor

    /**
     * 获取JVM规范版本（如：1.7）
     * @return
     */
    val jVMSpecVersion: String
        get() = runtime.specVersion

    /**
     * 获取JVM名称
     * @return
     */
    val jVMName: String
        get() = runtime.vmName

    /**
     * 获取Java的运行环境版本（如：1.7.0_67）
     * @return
     */
    val javaVersion: String
        get() = getSystemProperty("java.version")

    /**
     * 获取JVM运营商
     * @return
     */
    val jVMVendor: String
        get() = runtime.vmVendor

    /**
     * 获取JVM实现版本（如：25.102-b14）
     * @return
     */
    val jVMVersion: String
        get() = runtime.vmVersion

    /**
     * 获取JVM启动时间
     * @return
     */
    val jVMStartTimeMs: Long
        get() = runtime.startTime

    /**
     * 获取JVM运行时间
     * @return
     */
    val jVMUpTimeMs: Long
        get() = runtime.uptime

    /**
     * 获取JVM当前加载类总量
     * @return
     */
    val jVMLoadedClassCount: Long
        get() = classLoad.loadedClassCount.toLong()

    /**
     * 获取JVM已卸载类总量
     * @return
     */
    val jVMUnLoadedClassCount: Long
        get() = classLoad.unloadedClassCount

    /**
     * 获取JVM从启动到现在加载类总量
     * @return
     */
    val jVMTotalLoadedClassCount: Long
        get() = classLoad.totalLoadedClassCount

    /**
     * 获取JIT编译器名称
     * @return
     */
    val jITName: String
        get() = if (null == compilation) "" else compilation.name

    /**
     * 获取JIT总编译时间
     * @return
     */
    val jITTimeMs: Long
        get() = if (null != compilation && compilation.isCompilationTimeMonitoringSupported) {
            compilation.totalCompilationTime
        } else -1

    /**
     * 获取指定key的属性值
     * @param key
     * @return
     */
    fun getSystemProperty(key: String): String {
        return systemProperty.getProperty(key)
    }
}