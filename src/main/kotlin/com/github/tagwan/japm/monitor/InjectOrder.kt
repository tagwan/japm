package com.github.tagwan.japm.monitor

/**
 * 注入顺序
 *
 * @author jdg
 */
@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class InjectOrder(
    val order: Byte = 0
)