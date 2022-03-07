package com.github.tagwan.japm.collect

/**
 * 采集器
 *
 * @author jdg
 */
interface ICollect {

    /**
     * Is target
     *
     * @param classLoader
     * @param className
     * @param classfileBuffer
     * @return
     */
    fun isTarget(classLoader: ClassLoader, className: String, classfileBuffer: ByteArray): Boolean

    /**
     * Transform
     *
     * @param classLoader
     * @param className
     * @param classfileBuffer
     * @return
     */
    fun transform(classLoader: ClassLoader, className: String, classfileBuffer: ByteArray): ByteArray
}