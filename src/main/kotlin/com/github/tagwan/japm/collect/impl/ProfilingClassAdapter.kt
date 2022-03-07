//package com.github.tagwan.japm.collect.impl
//
//
//import com.github.tagwan.japm.Echo
//import org.objectweb.asm.ClassVisitor
//import org.objectweb.asm.FieldVisitor
//import org.objectweb.asm.MethodVisitor
//import org.objectweb.asm.Opcodes
//import org.slf4j.LoggerFactory
//import java.util.*
//
//
///**
// * Created by LinShunkang on 2018/4/15
// */
//class ProfilingClassAdapter(
//    cv: ClassVisitor,
//    private val innerClassName: String
//) : ClassVisitor(Opcodes.ASM9, cv) {
//
//    private val fullClassName: String
//    private val simpleClassName: String
//    private val classLevel: String
//    private var isInterface = false
//    private var isInvocationHandler = false
//    private val fieldNameList: MutableList<String> = ArrayList()
//
//    init {
//        fullClassName = innerClassName.replace('/', '.')
////        simpleClassName = TypeDescUtils.getSimpleClassName(innerClassName)
////        classLevel = LevelMappingFilter.getClassLevel(simpleClassName)
//    }
//
//
//    override fun visit(version: Int, access: Int, name: String, signature: String, superName: String, interfaces: Array<String>) {
//        logger.debug("ProfilingClassAdapter.visit(" + version + ", " + access + ", " + name + ", "
//            + signature + ", " + superName + ", " + Arrays.toString(interfaces) + ")")
//        super.visit(version, access, name, signature, superName, interfaces)
//        isInterface = access and Opcodes.ACC_INTERFACE != 0
//        isInvocationHandler = isInvocationHandler(interfaces)
//    }
//
//    private fun isInvocationHandler(interfaces: Array<String>?): Boolean {
//        if (interfaces == null || interfaces.size <= 0) {
//            return false
//        }
//        for (i in interfaces.indices) {
//            if ("java/lang/reflect/InvocationHandler" == interfaces[i]) {
//                return true
//            }
//        }
//        return false
//    }
//
//    override fun visitField(access: Int, name: String, desc: String, signature: String, value: Any): FieldVisitor {
//        val upFieldName = name.substring(0, 1).toUpperCase() + name.substring(1)
//        fieldNameList.add("get$upFieldName")
//        fieldNameList.add("set$upFieldName")
//        fieldNameList.add("is$upFieldName")
//        return super.visitField(access, name, desc, signature, value)
//    }
//
//    override fun visitMethod(
//        access: Int,
//        name: String,
//        desc: String,
//        signature: String,
//        exceptions: Array<String>
//    ): MethodVisitor? {
//        if (isInterface || !isNeedVisit(access, name)) {
//            return super.visitMethod(access, name, desc, signature, exceptions)
//        }
//        var classMethodName = "$simpleClassName.$name"
////        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
////            return super.visitMethod(access, name, desc, signature, exceptions)
////        }
////        val desc4Human: String = TypeDescUtils.getMethodParamsDesc(desc)
////        classMethodName = classMethodName + desc4Human
////        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
////            return super.visitMethod(access, name, desc, signature, exceptions)
////        }
//
//        val mv = cv.visitMethod(access, name, desc, signature, exceptions) ?: return null
//        logger.debug("ProfilingClassAdapter.visitMethod(" + access + ", " + name + ", " + desc + ", "
//            + signature + ", " + Arrays.toString(exceptions) + "), innerClassName=" + innerClassName)
//        return if (isInvocationHandler && isInvokeMethod(name, desc)) {
//            ProfilingDynamicMethodVisitor(access, name, desc, mv)
//        } else {
//            ProfilingMethodVisitor(access, name, desc, mv, innerClassName, fullClassName, simpleClassName,
//                classLevel, desc4Human)
//        }
//    }
//
//    private fun isNeedVisit(access: Int, name: String): Boolean {
//        //不对私有方法进行注入
//        if (access and Opcodes.ACC_PRIVATE != 0) {
//            return false
//        }
//
//        //不对抽象方法、native方法、桥接方法、合成方法进行注入
//        if (access and Opcodes.ACC_ABSTRACT != 0 || access and Opcodes.ACC_NATIVE != 0 || access and Opcodes.ACC_BRIDGE != 0 || access and Opcodes.ACC_SYNTHETIC != 0) {
//            return false
//        }
//        if ("<init>" == name || "<clinit>" == name) {
//            return false
//        }
//        return !fieldNameList.contains(name)
//    }
//
//    private fun isInvokeMethod(methodName: String, methodDesc: String): Boolean {
//        return methodName == "invoke" && methodDesc ==
//            "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"
//    }
//
//    companion object {
//        private val logger = LoggerFactory.getLogger(Echo::class.java)
//    }
//}