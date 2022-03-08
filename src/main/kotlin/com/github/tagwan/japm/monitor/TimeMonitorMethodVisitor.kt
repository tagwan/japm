package com.github.tagwan.japm.monitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TimeMonitorMethodVisitor(
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
            "com/github/tagwan/japm/monitor/TimeMonitor",
            "INSTANCE",
            "Lcom/github/tagwan/japm/monitor/TimeMonitor;"
        )

        mv.visitLdcInsn(key)

        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "com/github/tagwan/japm/monitor/TimeMonitor",
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
                "com/github/tagwan/japm/monitor/TimeMonitor", "INSTANCE", "Lcom/github/tagwan/japm/monitor/TimeMonitor;");

            mv.visitLdcInsn(key)

            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/github/tagwan/japm/monitor/TimeMonitor",
                "end",
                "(Ljava/lang/String;)V",
                false
            )
        }
        super.visitInsn(opcode)
    }
}