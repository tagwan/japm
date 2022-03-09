package com.github.tagwan.japm.core

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class BytesBuilder {

    lateinit var loader: ClassLoader
    lateinit var cr: ClassReader
    lateinit var cw : ClassWriter
    lateinit var cv: ClassVisitor

    fun classLoader(loader: ClassLoader): BytesBuilder {
        this.loader = loader
        return this
    }

    fun reader(classFileBuffer: ByteArray): BytesBuilder {
        this.cr = ClassReader(classFileBuffer)
        return this
    }

    fun writer(): BytesBuilder {
        val flag = if (isComputeMaxes()) ClassWriter.COMPUTE_MAXS else ClassWriter.COMPUTE_FRAMES
        this.cw = ClassWriter(cr, flag)
        return this
    }

    fun visitor(): BytesBuilder {
        this.cv = MonitorClassVisitor(cw)
        return this
    }

    fun build(): ByteArray {
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    private fun isComputeMaxes(): Boolean {

        val loaderName = loader.javaClass.name
        return loaderName == "org.apache.catalina.loader.WebappClassLoader"
            || loaderName == "org.apache.catalina.loader.ParallelWebappClassLoader"
            || loaderName == "org.springframework.boot.loader.LaunchedURLClassLoader"
            || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders")
    }
}