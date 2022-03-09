package com.github.tagwan.japm.core

import com.github.tagwan.japm.monitor.TimeMonitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MonitorMethodVisitor(
    api: Int,
    mv: MethodVisitor,
    private val key: String
) : MethodVisitor(api, mv) {

    /**
     * 方法开始时回调
     *
     */
    override fun visitCode() {
        mv.visitFieldInsn(
            Opcodes.GETSTATIC,
            TimeMonitor.clazzName,
            "INSTANCE",
            "L${TimeMonitor.clazzName};"
        )

        mv.visitLdcInsn(key)

        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            TimeMonitor.clazzName,
            "start",
            "(Ljava/lang/String;)V",
            false
        )
    }

    /**
     * Visit insn
     * <p>
     *     在每个 return 指令前插入 TimeMonitor.end
     *
     * @param opcode
     */
    override fun visitInsn(opcode: Int) {
        if (opcode in Opcodes.IRETURN..Opcodes.RETURN) {

            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                TimeMonitor.clazzName,
                "INSTANCE",
                "L${TimeMonitor.clazzName};"
            )

            mv.visitLdcInsn(key)

            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                TimeMonitor.clazzName,
                "end",
                "(Ljava/lang/String;)V",
                false
            )
        }
        super.visitInsn(opcode)
    }
}