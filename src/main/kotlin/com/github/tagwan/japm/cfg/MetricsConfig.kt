package com.github.tagwan.japm.cfg

/**
 * 一些指标的配置
 *
 * @constructor
 *
 * @param path
 */
class MetricsConfig(path: String) : AbstractProperties(path, "metrics.") {
    // 最小执行的时间
    val minTime: Long by prop
}