package com.github.tagwan.japm.collect

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

abstract class AbstractCollect {

    fun getBytes(
        loader: ClassLoader,
        className: String,
        classFileBuffer: ByteArray
    ): ByteArray {
        return if (needComputeMaxes(loader)) {
            val cr = ClassReader(classFileBuffer)
            val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            val cv: ClassVisitor = getClassVisitor(cw, className)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            cw.toByteArray()
        } else {
            val cr = ClassReader(classFileBuffer)
            val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES)
            val cv: ClassVisitor = getClassVisitor(cw, className)
            cr.accept(cv, ClassReader.EXPAND_FRAMES)
            cw.toByteArray()
        }
    }

    protected open fun needComputeMaxes(classLoader: ClassLoader?): Boolean {
        if (classLoader == null) {
            return false
        }
        val loaderName = classLoader.javaClass.name
        return loaderName == "org.apache.catalina.loader.WebappClassLoader"
            || loaderName == "org.apache.catalina.loader.ParallelWebappClassLoader"
            || loaderName == "org.springframework.boot.loader.LaunchedURLClassLoader"
            || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders")
    }

    abstract fun getClassVisitor(cw: ClassWriter, clazzName: String): ClassVisitor
}