package com.github.tagwan.japm.monitor

/**
 * I monitor
 *
 * @constructor Create empty I monitor
 */
interface IMonitor {

    fun injectOnStart(key: String)

    fun injectOnOver(key: String)
}