package com.github.tagwan.japm.agent

import com.github.tagwan.japm.collect.ICollect
import com.github.tagwan.japm.collect.impl.TimeCollector
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class ApmTransformer : ClassFileTransformer {

    var collectors: List<ICollect> = arrayListOf(TimeCollector())

    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain,
        classfileBuffer: ByteArray
    ): ByteArray? {
        if (className == null || loader == null
            || loader.javaClass.name == "sun.reflect.DelegatingClassLoader"
            || loader.javaClass.name == "javax.management.remote.rmi"
            || className.indexOf("\$Proxy") != -1
            || className.startsWith("java")
        ) {
            return null
        }

        collectors.forEach { c ->
            if (c.isTarget(loader, className, classfileBuffer)) {
                return c.transform(loader, className, classfileBuffer)
            }
        }

        return classfileBuffer
    }
}