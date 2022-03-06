package com.github.tagwan.japm

import com.github.tagwan.japm.monitor.MonitorTest
import com.github.tagwan.japm.monitor.TimeMonitorClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.V11

class Echo {
}


fun main() {
    val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
    val monitor = TimeMonitorClassVisitor(cw)
    monitor.visit(
        V11,
        ACC_PUBLIC,
        "com/github/tagwan/japm/monitor/MonitorTest",
        null,
        "java/lang/Object",
        null)

    MonitorTest().test()
}