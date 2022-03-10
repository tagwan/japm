package com.github.tagwan.japm.internal

import com.github.tagwan.japm.data.JVMMemoryUsage
import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.MemoryPoolMXBean
import java.util.*


/**
 * JVM内存信息工具类
 *
 * @constructor Create empty J v m memory utils
 */
object JVMMemoryUtils {

    private val memoryMXBean: MemoryMXBean = ManagementFactory.getMemoryMXBean()
    private lateinit var edenSpaceMxBean: MemoryPoolMXBean
    private lateinit var survivorSpaceMxBean: MemoryPoolMXBean
    private lateinit var oldGenMxBean: MemoryPoolMXBean
    private lateinit var permGenMxBean: MemoryPoolMXBean
    private lateinit var codeCacheMxBean: MemoryPoolMXBean

    init {
        val memoryPoolMXBeanList = ManagementFactory.getMemoryPoolMXBeans()
        for (memoryPoolMXBean in memoryPoolMXBeanList) {
            val poolName = memoryPoolMXBean.name ?: continue
            // 官方JVM(HotSpot)提供的MemoryPoolMXBean
            // JDK1.7/1.8 Eden区内存池名称： "Eden Space" 或  "PS Eden Space"、 “G1 Eden Space”(和垃圾收集器有关)
            // JDK1.7/1.8 Survivor区内存池名称："Survivor Space" 或 "PS Survivor Space"、“G1 Survivor Space”(和垃圾收集器有关)
            // JDK1.7  老区内存池名称： "Tenured Gen"
            // JDK1.8  老区内存池名称："Old Gen" 或 "PS Old Gen"、“G1 Old Gen”(和垃圾收集器有关)
            // JDK1.7  方法/永久区内存池名称： "Perm Gen" 或 "PS Perm Gen"(和垃圾收集器有关)
            // JDK1.8  方法/永久区内存池名称："Metaspace"(注意：不在堆内存中)
            // JDK1.7/1.8  CodeCache区内存池名称： "Code Cache"
            when {
                poolName.endsWith("Eden Space") -> {
                    edenSpaceMxBean = memoryPoolMXBean
                }
                poolName.endsWith("Survivor Space") -> {
                    survivorSpaceMxBean = memoryPoolMXBean
                }
                poolName.endsWith("Tenured Gen") || poolName.endsWith("Old Gen") -> {
                    oldGenMxBean = memoryPoolMXBean
                }
                poolName.endsWith("Perm Gen") || poolName.endsWith("Metaspace") -> {
                    permGenMxBean = memoryPoolMXBean
                }
                poolName.endsWith("Code Cache") -> {
                    codeCacheMxBean = memoryPoolMXBean
                }
            }
        }
    }

    /**
     * 获取堆内存情况
     * @return 不能获取到返回null
     */
    val heapMemoryUsage: JVMMemoryUsage?
        get() {
            val usage = memoryMXBean.heapMemoryUsage
            if (usage != null) {
                return JVMMemoryUsage(usage)
            }
            return null
        }

    /**
     * 获取堆外内存情况
     * @return 不能获取到返回null
     */
    val nonHeapMemoryUsage: JVMMemoryUsage?
        get() {
            val usage = memoryMXBean.nonHeapMemoryUsage
            if (usage != null) {
                return JVMMemoryUsage(usage)
            }
            return null
        }

    /**
     * 获取Eden区内存情况
     * @return 不能获取到返回null
     */
    val edenSpaceMemoryUsage: JVMMemoryUsage?
        get() = getMemoryPoolUsage(edenSpaceMxBean)

    /**
     * 获取Eden区内存峰值（从启动或上一次重置开始统计），并重置
     * @return 不能获取到返回null
     */
    val andResetEdenSpaceMemoryPeakUsage: JVMMemoryUsage?
        get() = getAndResetMemoryPoolPeakUsage(edenSpaceMxBean)

    /**
     * 获取Survivor区内存情况
     * @return 不能获取到返回null
     */
    val survivorSpaceMemoryUsage: JVMMemoryUsage?
        get() = getMemoryPoolUsage(survivorSpaceMxBean)

    /**
     * 获取Survivor区内存峰值（从启动或上一次重置开始统计），并重置
     * @return 不能获取到返回null
     */
    val andResetSurvivorSpaceMemoryPeakUsage: JVMMemoryUsage?
        get() = getAndResetMemoryPoolPeakUsage(survivorSpaceMxBean)

    /**
     * 获取老区内存情况
     * @return 不能获取到返回null
     */
    val oldGenMemoryUsage: JVMMemoryUsage?
        get() = getMemoryPoolUsage(oldGenMxBean)

    /**
     * 获取老区内存峰值（从启动或上一次重置开始统计），并重置
     * @return 不能获取到返回null
     */
    val andResetOldGenMemoryPeakUsage: JVMMemoryUsage?
        get() = getAndResetMemoryPoolPeakUsage(oldGenMxBean)

    /**
     * 获取永久区/方法区内存情况
     * @return 不能获取到返回null
     */
    val permGenMemoryUsage: JVMMemoryUsage?
        get() = getMemoryPoolUsage(permGenMxBean)

    /**
     * 获取永久区/方法区内存峰值（从启动或上一次重置开始统计），并重置
     * @return 不能获取到返回null
     */
    val andResetPermGenMemoryPeakUsage: JVMMemoryUsage?
        get() = getAndResetMemoryPoolPeakUsage(permGenMxBean)

    /**
     * 获取CodeCache区内存情况
     * @return 不能获取到返回null
     */
    val codeCacheMemoryUsage: JVMMemoryUsage?
        get() = getMemoryPoolUsage(codeCacheMxBean)

    /**
     * 获取CodeCache区内存峰值（从启动或上一次重置开始统计），并重置
     * @return 不能获取到返回null
     */
    val andResetCodeCacheMemoryPeakUsage: JVMMemoryUsage?
        get() = getAndResetMemoryPoolPeakUsage(codeCacheMxBean)

    private fun getMemoryPoolUsage(memoryPoolMXBean: MemoryPoolMXBean?): JVMMemoryUsage? {
        if (memoryPoolMXBean != null) {
            val usage = memoryPoolMXBean.usage
            if (usage != null) {
                return JVMMemoryUsage(usage)
            }
        }
        return null
    }

    private fun getAndResetMemoryPoolPeakUsage(memoryPoolMXBean: MemoryPoolMXBean?): JVMMemoryUsage? {
        if (memoryPoolMXBean != null) {
            val usage = memoryPoolMXBean.peakUsage
            if (usage != null) {
                memoryPoolMXBean.resetPeakUsage()
                return JVMMemoryUsage(usage)
            }
        }
        return null
    }
}

fun main(args: Array<String>) {
    val listRoot: MutableList<List<Long>> = ArrayList()
    while (true) {
        println("=======================================================================")
        println("getHeapMemoryUsage: " + JVMMemoryUtils.heapMemoryUsage)
        println("getNonHeapMemoryUsage: " + JVMMemoryUtils.nonHeapMemoryUsage)
        println("getEdenSpaceMemoryUsage: " + JVMMemoryUtils.edenSpaceMemoryUsage)
        println("getAndResetEdenSpaceMemoryPeakUsage: " + JVMMemoryUtils.andResetEdenSpaceMemoryPeakUsage)
        println("getSurvivorSpaceMemoryUsage: " + JVMMemoryUtils.survivorSpaceMemoryUsage)
        println("getAndResetSurvivorSpaceMemoryPeakUsage: " + JVMMemoryUtils.andResetSurvivorSpaceMemoryPeakUsage)
        println("getOldGenMemoryUsage: " + JVMMemoryUtils.oldGenMemoryUsage)
        println("getAndResetOldGenMemoryPeakUsage: " + JVMMemoryUtils.andResetOldGenMemoryPeakUsage)
        println("getPermGenMemoryUsage: " + JVMMemoryUtils.permGenMemoryUsage)
        println("getAndResetPermGenMemoryPeakUsage: " + JVMMemoryUtils.andResetPermGenMemoryPeakUsage)
        println("getCodeCacheMemoryUsage: " + JVMMemoryUtils.codeCacheMemoryUsage)
        println("getAndResetCodeCacheMemoryPeakUsage: " + JVMMemoryUtils.andResetCodeCacheMemoryPeakUsage)
        val list: ArrayList<Long> = ArrayList(10000)
        listRoot.add(list)
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (list.size > 1) {
            list.removeAt(0)
        }
        Runtime.getRuntime().gc()
    }
}