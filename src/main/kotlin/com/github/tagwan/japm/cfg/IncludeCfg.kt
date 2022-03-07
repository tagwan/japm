package com.github.tagwan.japm.cfg

object IncludeCfg {
    var interfaceName: String = ""
    var baseClazzName: String = ""
    var methodName: String = ""

    fun validate(clazzName: String): Boolean {
        return true
    }
}