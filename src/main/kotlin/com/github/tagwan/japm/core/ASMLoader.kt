package com.github.tagwan.japm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * ASM loader
 *
 * @author jdg
 */
class ASMLoader private constructor(
    private val builder: Builder
) {

    fun toBytes(): ByteArray {
        return this.builder.cw.toByteArray()
    }

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        lateinit var loader: ClassLoader
        lateinit var cr: ClassReader
        lateinit var cw: ClassWriter
        lateinit var cv: ClassVisitor

        fun classLoader(loader: ClassLoader) {
            this.loader = loader
        }

        fun reader(classFileBuffer: ByteArray) {
            this.cr = ClassReader(classFileBuffer)
        }

        fun writer() {
            val flag = if (isComputeMaxes()) ClassWriter.COMPUTE_MAXS else ClassWriter.COMPUTE_FRAMES
            this.cw = ClassWriter(cr, flag)
        }

        fun visitor() {
            this.cv = MonitorClassVisitor(cw)
        }

        fun build(): ASMLoader {
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            return ASMLoader(this)
        }

        private fun isComputeMaxes() = loader.javaClass.name == "org.apache.catalina.loader.WebappClassLoader"
            || loader.javaClass.name == "org.apache.catalina.loader.ParallelWebappClassLoader"
            || loader.javaClass.name == "org.springframework.boot.loader.LaunchedURLClassLoader"
            || loader.javaClass.name.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders")
    }

}
