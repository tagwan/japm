package com.github.tagwan.japm.cfg


class RequireConfig(path: String) : AbstractProperties(path, "require.") {
    val packageName: String by prop
    val interfaceName: String by prop
    val baseName: String by prop
    val clazzName: String by prop
    val methodName: String by prop
}