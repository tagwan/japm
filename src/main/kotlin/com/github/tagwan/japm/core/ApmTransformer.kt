package com.github.tagwan.japm.core

import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

/**
 * Apm transformer
 *
 * @author jdg
 */
class ApmTransformer : ClassFileTransformer {

    override fun transform(
        loader: ClassLoader?,
        className: String?,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain,
        classfileBuffer: ByteArray
    ): ByteArray? {
        if (loader == null || className == null)
            return null

        if (!loader.validateAPM()) {
            return null
        }

        if (!className.validateAPM()) {
            return null
        }

        val asm = ASMLoader.build {
            classLoader(loader)
            reader(classfileBuffer)
            writer()
            visitor()
        }
        return asm.toBytes()
    }

    private fun ClassLoader.validateAPM(): Boolean {
        if (this.javaClass.name == "sun.reflect.DelegatingClassLoader"
            || this.javaClass.name == "javax.management.remote.rmi"
        ) {
            return false
        }

        return true
    }


    private fun String.validateAPM(): Boolean {
        if (this.indexOf("\$Proxy") != -1
            || this.startsWith("java")
            || this.startsWith("sun")
            || this.startsWith("com/sun")
            || this.startsWith("com/intellij")
            || this.startsWith("org/objectweb/asm")
            || this.startsWith("com/github/tagwan/japm")
        ) {
            return false
        }

        return true
    }
}