package com.github.tagwan.japm.monitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TimeMonitorClassVisitor(cw: ClassWriter) : ClassVisitor(Opcodes.ASM9, cw) {

    private lateinit var className: String

    /**
     * 访问类时回调
     */
    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
    }

    /**
     * 访问到方法时回调
     */
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor = object : MethodVisitor(
        api,
        cv.visitMethod(access, name, descriptor, signature, exceptions)
    ) {
        // 方法开始时回调
        override fun visitCode() {
            if (className == "com/github/tagwan/japm/monitor/TimeMonitor")
                return super.visitCode()



            // 方法的参数
            mv.visitLdcInsn("${className}-${name}-${descriptor}")

            // 调用 TimeMonitor.start
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "com/github/tagwan/japm/monitor/TimeMonitor",
                "start",
                "(Ljava/lang/String;)V",
                false
            )
        }

        override fun visitInsn(opcode: Int) {
            if (className == "com/github/tagwan/japm/monitor/TimeMonitor")
                return super.visitInsn(opcode)

            // 在每个 return 指令前插入 TimeMonitor.end
            if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {
                // 方法的参数
                mv.visitLdcInsn("${className}-${name}-${descriptor}")
                // 调用 TimeMonitor.start
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "com/github/tagwan/japm/monitor/TimeMonitor",
                    "end",
                    "(Ljava/lang/String;)V",
                    false
                )
            }
            super.visitInsn(opcode)
        }
    }

}
