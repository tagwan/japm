package com.github.tagwan.japm.internal

import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory
import java.util.*


/**
 * JVM GC信息工具类
 *
 * @constructor Create empty J v m g c utils
 */
object JVMGCUtils {
    private lateinit var youngGC: GarbageCollectorMXBean
    private lateinit var fullGC: GarbageCollectorMXBean

    // YGC名称
    val youngGCName: String
        get() = youngGC.name

    // YGC总次数
    val youngGCCollectionCount: Long
        get() = youngGC.collectionCount

    // YGC总时间
    val youngGCCollectionTime: Long
        get() = youngGC.collectionTime

    // FGC名称
    val fullGCName: String
        get() = fullGC.name

    // FGC总次数
    val fullGCCollectionCount: Long
        get() = fullGC.collectionCount

    // FGC总次数
    val fullGCCollectionTime: Long
        get() = fullGC.collectionTime

    init {
        val gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans()
        for (gcMXBean in gcMXBeans) {
            val gcName = gcMXBean.name
                ?: continue
            //G1 Old Generation
            //Garbage collection optimized for short pausetimes Old Collector
            //Garbage collection optimized for throughput Old Collector
            //Garbage collection optimized for deterministic pausetimes Old Collector
            //G1 Young Generation
            //Garbage collection optimized for short pausetimes Young Collector
            //Garbage collection optimized for throughput Young Collector
            //Garbage collection optimized for deterministic pausetimes Young Collector

            if ((gcName.endsWith("Old Generation")
                    || "ConcurrentMarkSweep" == gcName || "MarkSweepCompact" == gcName || "PS MarkSweep" == gcName)) {
                fullGC = gcMXBean
            } else if ((gcName.endsWith("Young Generation")
                    || "ParNew" == gcName || "Copy" == gcName || "PS Scavenge" == gcName)) {
                youngGC = gcMXBean
            }
        }
    }
}