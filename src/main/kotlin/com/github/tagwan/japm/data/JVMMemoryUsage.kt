package com.github.tagwan.japm.data

import java.lang.management.MemoryUsage

/**
 * JVM内存区域使用情况。
 * <pre>
 *     init：初始内存大小（字节）
 *     used：当前使用内存大小（字节）
 *     committed：已经申请分配的内存大小（字节）
 *     max：最大内存大小（字节）
 *     usedPercent：已经申请分配内存与最大内存大小的百分比
 * </pre>
 *
 * @param init         初始内存大小（字节）
 * @param used         当前使用内存大小（字节）
 * @param committed    已经申请分配的内存大小（字节）
 * @param max          最大内存大小（字节）
 * @author jdg
 */
data class JVMMemoryUsage constructor(
    var init: Long = 0,
    var used: Long = 0,
    var committed: Long = 0,
    var max: Long = 0

) {

    // 已经申请分配内存与最大内存大小的百分比
    private var usedPercent: Float = 0f

    constructor(memoryUsage: MemoryUsage) : this(memoryUsage.init, memoryUsage.used, memoryUsage.committed, memoryUsage.max) {
        this.usedPercent = if (this.used > 0 && max > 0) {
            used * java.lang.Float.valueOf("1.0") / max
        } else {
            0f
        }
    }

    override fun toString(): String {
        val buf = StringBuffer()
        buf.append("init = " + init + "(" + (init shr 10) + "K) ")
        buf.append("used = " + used + "(" + (used shr 10) + "K) ")
        buf.append("committed = " + committed + "(" +
            (committed shr 10) + "K) ")
        buf.append("max = " + max + "(" + (max shr 10) + "K)")
        buf.append("usedPercent = $usedPercent")
        return buf.toString()
    }
}