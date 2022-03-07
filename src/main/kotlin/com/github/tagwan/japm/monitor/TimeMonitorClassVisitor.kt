package com.github.tagwan.japm.monitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.GETSTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL

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

        /**
         * 方法开始时回调
         *
         */
        override fun visitCode() {
            mv.visitFieldInsn(
                GETSTATIC,
                "com/github/tagwan/japm/monitor/TimeMonitor",
                "INSTANCE",
                "Lcom/github/tagwan/japm/monitor/TimeMonitor;"
            )

            // 方法的参数
            mv.visitLdcInsn("${className}-${name}-${descriptor}")

            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "com/github/tagwan/japm/monitor/TimeMonitor",
                "start",
                "(Ljava/lang/String;)V",
                false
            )
        }

        override fun visitInsn(opcode: Int) {
            // 在每个 return 指令前插入 TimeMonitor.end
            if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {

                mv.visitFieldInsn(
                    GETSTATIC,
                    "com/github/tagwan/japm/monitor/TimeMonitor", "INSTANCE", "Lcom/github/tagwan/japm/monitor/TimeMonitor;");

                mv.visitLdcInsn("${className}-${name}-${descriptor}")

                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
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
