package com.github.tagwan.japm.core

import com.github.tagwan.japm.collect.impl.SimpleCollector
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

class ApmTransformer : ClassFileTransformer {

    private val collector = SimpleCollector()

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

        if (collector.isTarget(loader, className, classfileBuffer)) {
            return collector.transform(loader, className, classfileBuffer)
        }
        return classfileBuffer
    }
}