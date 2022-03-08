package com.github.tagwan.japm.collect.impl

import com.github.tagwan.japm.cfg.ConfigMgr
import com.github.tagwan.japm.collect.AbstractCollect
import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.monitor.TimeMonitorClassVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class TimeCollector : AbstractCollect(), ICollect {
    override fun getClassVisitor(cw: ClassWriter, clazzName: String): ClassVisitor {
        return TimeMonitorClassVisitor(cw)
    }

    override fun isTarget(classLoader: ClassLoader, clazzName: String, classfileBuffer: ByteArray): Boolean {
        if (classLoader.javaClass.name == "sun.reflect.DelegatingClassLoader"
            || classLoader.javaClass.name == "javax.management.remote.rmi"
            || clazzName.indexOf("\$Proxy") != -1
            || clazzName.startsWith("java")
            || clazzName.startsWith("sun")
            || clazzName.startsWith("com/sun")
            || clazzName.startsWith("com/intellij")
            || clazzName.startsWith("org/objectweb/asm")
            || clazzName.startsWith("com/github/tagwan/japm")
        ) {
            return false
        }

        return ConfigMgr.validatePackage(clazzName)
    }

    override fun transform(classLoader: ClassLoader, clazzName: String, classfileBuffer: ByteArray): ByteArray {
        return super.getBytes(classLoader, clazzName, classfileBuffer)
    }
}