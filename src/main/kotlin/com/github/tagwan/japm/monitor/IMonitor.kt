package com.github.tagwan.japm.monitor

/**
 * 监控接口
 *
 * @constructor Create empty I monitor
 */
interface IMonitor {

    fun injectOnStart(key: String)

    fun injectOnOver(key: String)
}