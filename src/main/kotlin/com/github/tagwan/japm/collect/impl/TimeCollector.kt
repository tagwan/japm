package com.github.tagwan.japm.collect.impl

import com.github.tagwan.japm.collect.AbstractCollect
import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.internal.PropertiesUtils
import com.github.tagwan.japm.monitor.TimeMonitorClassVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class TimeCollector : AbstractCollect(), ICollect {
    override fun getClassVisitor(cw: ClassWriter, className: String): ClassVisitor {
        return TimeMonitorClassVisitor(cw)
    }

    override fun isTarget(classLoader: ClassLoader, className: String, classfileBuffer: ByteArray): Boolean {
        println("className-->$className")
        return className == "com/github/tagwan/japm/Echo"
    }

    override fun transform(classLoader: ClassLoader, className: String, classfileBuffer: ByteArray): ByteArray {
        return super.getBytes(classLoader, className, classfileBuffer)
    }
}