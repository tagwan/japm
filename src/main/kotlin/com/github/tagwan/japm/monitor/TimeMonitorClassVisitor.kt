package com.github.tagwan.japm.monitor

import org.objectweb.asm.*
import org.slf4j.LoggerFactory
import java.util.*

class TimeMonitorClassVisitor(cw: ClassWriter) : ClassVisitor(Opcodes.ASM9, cw) {

    private lateinit var className: String
    private var isInterface: Boolean = false
    private val fieldNameList: ArrayList<String> = ArrayList()

    /**
     * 访问类时回调
     */
    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
        isInterface = access and Opcodes.ACC_INTERFACE != 0
    }


    override fun visitField(access: Int, name: String, descriptor: String?, signature: String?, value: Any?): FieldVisitor {
        val upFieldName = name.substring(0, 1).toUpperCase() + name.substring(1)
        fieldNameList.add("get$upFieldName")
        fieldNameList.add("set$upFieldName")
        fieldNameList.add("is$upFieldName")
        return super.visitField(access, name, descriptor, signature, value)
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
    ): MethodVisitor {

        if (isInterface || isNeedVisit(access, name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions)
        }

        totals++
        return TimeMonitorMethodVisitor(
            api,
            cv.visitMethod(access, name, descriptor, signature, exceptions),
            "${className}-${name}-${descriptor}"
        )
    }

    private fun isNeedVisit(access: Int, name: String): Boolean {
        //不对私有方法进行注入
        if (access and Opcodes.ACC_PRIVATE != 0) {
            return false
        }

        //不对抽象方法、native方法、桥接方法、合成方法进行注入
        if (access and Opcodes.ACC_ABSTRACT != 0 || access and Opcodes.ACC_NATIVE != 0 || access and Opcodes.ACC_BRIDGE != 0 || access and Opcodes.ACC_SYNTHETIC != 0) {
            return false
        }

        if ("<init>" == name || "<clinit>" == name) {
            return false
        }
        return !fieldNameList.contains(name)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TimeMonitorClassVisitor::class.java)
        var totals = 0
    }
}
